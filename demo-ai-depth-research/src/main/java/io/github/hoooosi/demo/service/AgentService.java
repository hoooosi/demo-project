package io.github.hoooosi.demo.service;

import io.github.hoooosi.demo.vm.CommandExecutor;
import io.github.hoooosi.demo.vm.ExecutionResult;
import io.github.hoooosi.demo.vm.VirtualMachineManager;
import io.github.hoooosi.demo.websocket.AgentWebSocketHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent 研究服务
 * <p>
 * 原理：
 * 1. 用户提交研究任务（如"分析 Spring Boot 最新特性"）
 * 2. Agent 自动创建虚拟机
 * 3. Agent 使用 AI 规划执行步骤（搜索、下载、分析）
 * 4. Agent 在虚拟机中执行命令收集信息
 * 5. Agent 整合信息生成最终报告
 * 6. 全程实时推送到前端
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentService {

    private final ChatClient.Builder chatClientBuilder;
    private final VirtualMachineManager vmManager;
    private final CommandExecutor commandExecutor;
    private final AgentWebSocketHandler webSocketHandler;

    // 任务存储
    private final Map<String, ResearchTask> tasks = new ConcurrentHashMap<>();

    /**
     * 启动研究任务
     */
    public String startResearchTask(String query) {
        String taskId = UUID.randomUUID().toString();

        ResearchTask task = new ResearchTask();
        task.setTaskId(taskId);
        task.setQuery(query);
        task.setStatus(TaskStatus.CREATED);
        task.setCreatedAt(LocalDateTime.now());

        tasks.put(taskId, task);

        // 异步执行研究任务
        CompletableFuture.runAsync(() -> executeResearchTask(task));

        return taskId;
    }

    /**
     * 执行研究任务（Agent 主流程）
     */
    private void executeResearchTask(ResearchTask task) {
        try {
            task.setStatus(TaskStatus.RUNNING);
            broadcastTaskUpdate(task, "🤖 Agent 开始工作...");

            // 步骤 1: 创建虚拟机环境
            broadcastTaskUpdate(task, "📦 正在创建隔离的工作环境...");
            String containerId = vmManager.createAndStartVm();
            task.setContainerId(containerId);
            broadcastTaskUpdate(task, "✅ 工作环境创建成功");

            // 步骤 2: 初始化环境（安装必要工具）
            broadcastTaskUpdate(task, "🔧 正在安装必要工具...");
            initializeEnvironment(task);

            // 步骤 3: Agent 规划研究步骤
            broadcastTaskUpdate(task, "🧠 Agent 正在思考如何解决您的问题...");
            List<String> steps = planResearchSteps(task.getQuery());
            task.setSteps(steps);
            broadcastTaskUpdate(task, "📋 研究计划已生成，共 " + steps.size() + " 个步骤");

            // 步骤 4: 执行每个步骤
            List<String> collectedInfo = new ArrayList<>();
            for (int i = 0; i < steps.size(); i++) {
                String step = steps.get(i);
                broadcastTaskUpdate(task, String.format("⚡ [%d/%d] %s", i + 1, steps.size(), step));

                // Agent 执行步骤并收集信息
                String info = executeStep(task, step);
                collectedInfo.add(info);
            }

            // 步骤 5: Agent 整合信息生成报告
            broadcastTaskUpdate(task, "📊 Agent 正在整合收集到的信息...");
            String finalReport = synthesizeReport(task.getQuery(), collectedInfo);
            task.setResult(finalReport);

            // 步骤 6: 完成
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
            broadcastTaskUpdate(task, "✨ 研究完成！");
            broadcastFinalReport(task, finalReport);

        } catch (Exception e) {
            log.error("研究任务执行失败", e);
            task.setStatus(TaskStatus.FAILED);
            task.setError(e.getMessage());
            broadcastTaskUpdate(task, "❌ 任务失败: " + e.getMessage());
        } finally {
            // 清理虚拟机
            if (task.getContainerId() != null) {
                broadcastTaskUpdate(task, "🧹 正在清理工作环境...");
                vmManager.destroyVm();
            }
        }
    }

    /**
     * 初始化虚拟机环境
     */
    private void initializeEnvironment(ResearchTask task) {
        broadcastTaskUpdate(task, "🔍 检查工作环境...");

        // 检查是否为自定义镜像（通过检查 Node.js 是否存在）
        ExecutionResult checkResult = executeVmCommandSilent(task, "which node");

        if (checkResult.getSuccess()) {
            // 自定义镜像，工具已预装
            broadcastTaskUpdate(task, "✅ 检测到完整工作环境（Python、Node.js、Java 已就绪）");

            // 显示工具版本
            executeVmCommand(task, "python3 --version && node --version && java --version", "工具版本信息");
        } else {
            // 备用镜像，需要安装工具
            broadcastTaskUpdate(task, "⚠️ 使用备用环境，正在安装必要工具...");
            executeVmCommand(task, "apt-get update -y", "更新软件源");
            executeVmCommand(task, "apt-get install -y curl wget python3 python3-pip", "安装基础工具");
            broadcastTaskUpdate(task, "✅ 基础工具安装完成");
        }
    }

    /**
     * 使用 AI 规划研究步骤
     */
    private List<String> planResearchSteps(String query) {
        ChatClient chatClient = chatClientBuilder.build();

        String prompt = """
                你是一个研究助手 Agent。用户想要研究："%s"

                请为这个研究任务规划 3-5 个具体的执行步骤。每个步骤应该是一个可以在 Linux 虚拟机中执行的操作。

                可用的操作类型：
                1. 使用 curl 下载网页内容
                2. 使用 wget 下载文件
                3. 使用 python3 执行 Python 脚本分析数据
                4. 使用 grep/awk/sed 等工具处理文本
                5. 创建和读取文件

                请只输出步骤描述，每行一个步骤，格式如下：
                步骤1: 使用 curl 搜索相关信息
                步骤2: 下载文档进行分析
                步骤3: 整理关键信息

                不要输出其他内容。
                """.formatted(query);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return Arrays.stream(response.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * 执行单个研究步骤
     */
    private String executeStep(ResearchTask task, String step) {
        // 使用 AI 将步骤转换为具体的 shell 命令
        ChatClient chatClient = chatClientBuilder.build();

        String prompt = """
                将以下研究步骤转换为可以在 Ubuntu Linux 虚拟机中执行的 shell 命令。

                步骤描述：%s

                要求：
                1. 只输出一条可直接执行的命令
                2. 命令应该将结果保存到文件或直接输出
                3. 不要使用交互式命令
                4. 命令应该是安全的

                只输出命令本身，不要有任何解释。
                """.formatted(step);

        String command = chatClient.prompt()
                .user(prompt)
                .call()
                .content()
                .trim();

        // 在虚拟机中执行命令
        return executeVmCommand(task, command, step);
    }

    /**
     * 在虚拟机中执行命令并返回输出
     */
    private String executeVmCommand(ResearchTask task, String command, String description) {
        broadcastVmOperation(task, "💻 " + description);
        broadcastVmOperation(task, "$ " + command);

        try {
            ExecutionResult result = commandExecutor.executeCommand(command, output -> {
                // 实时推送命令输出到前端
                broadcastVmOutput(task, output);
            }).join();

            if (result.getSuccess()) {
                return result.getStdout();
            } else {
                broadcastVmOperation(task, "⚠️ 命令执行失败: " + result.getStderr());
                return "执行失败: " + result.getStderr();
            }
        } catch (Exception e) {
            broadcastVmOperation(task, "❌ 命令执行异常: " + e.getMessage());
            return "执行异常: " + e.getMessage();
        }
    }

    /**
     * 静默执行命令（不推送到前端，用于环境检查）
     */
    private ExecutionResult executeVmCommandSilent(ResearchTask task, String command) {
        try {
            return commandExecutor.executeCommand(command, output -> {
                // 静默执行，不推送输出
            }).join();
        } catch (Exception e) {
            return ExecutionResult.builder()
                    .command(command)
                    .success(false)
                    .exitCode(-1)
                    .stderr(e.getMessage())
                    .build();
        }
    }

    /**
     * 整合信息生成最终报告
     */
    private String synthesizeReport(String query, List<String> collectedInfo) {
        ChatClient chatClient = chatClientBuilder.build();

        StringBuilder infoBuilder = new StringBuilder();
        for (int i = 0; i < collectedInfo.size(); i++) {
            infoBuilder.append("信息源 ").append(i + 1).append(":\n");
            infoBuilder.append(collectedInfo.get(i)).append("\n\n");
        }

        String prompt = """
                你是一个研究报告撰写专家。请根据 Agent 收集到的信息，为用户的问题生成一份详细的研究报告。

                用户问题：%s

                收集到的信息：
                %s

                请生成一份结构化的研究报告，包含：
                1. 摘要
                2. 详细分析
                3. 关键发现
                4. 结论和建议

                报告应该专业、客观、易懂。
                """.formatted(query, infoBuilder.toString());

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 广播任务更新
     */
    private void broadcastTaskUpdate(ResearchTask task, String message) {
        webSocketHandler.broadcast(Map.of(
                "type", "TASK_UPDATE",
                "taskId", task.getTaskId(),
                "status", task.getStatus().name(),
                "message", message,
                "timestamp", LocalDateTime.now().toString()));
    }

    /**
     * 广播虚拟机操作
     */
    private void broadcastVmOperation(ResearchTask task, String message) {
        webSocketHandler.broadcast(Map.of(
                "type", "VM_OPERATION",
                "taskId", task.getTaskId(),
                "message", message,
                "timestamp", LocalDateTime.now().toString()));
    }

    /**
     * 广播虚拟机输出
     */
    private void broadcastVmOutput(ResearchTask task, String output) {
        webSocketHandler.broadcast(Map.of(
                "type", "VM_OUTPUT",
                "taskId", task.getTaskId(),
                "output", output,
                "timestamp", LocalDateTime.now().toString()));
    }

    /**
     * 广播最终报告
     */
    private void broadcastFinalReport(ResearchTask task, String report) {
        webSocketHandler.broadcast(Map.of(
                "type", "FINAL_REPORT",
                "taskId", task.getTaskId(),
                "report", report,
                "timestamp", LocalDateTime.now().toString()));
    }

    /**
     * 获取任务状态
     */
    public Map<String, Object> getTaskStatus(String taskId) {
        ResearchTask task = tasks.get(taskId);
        if (task == null) {
            return Map.of("success", false, "message", "任务不存在");
        }

        return Map.of(
                "success", true,
                "taskId", task.getTaskId(),
                "query", task.getQuery(),
                "status", task.getStatus().name(),
                "createdAt", task.getCreatedAt().toString());
    }

    /**
     * 获取任务结果
     */
    public Map<String, Object> getTaskResult(String taskId) {
        ResearchTask task = tasks.get(taskId);
        if (task == null) {
            return Map.of("success", false, "message", "任务不存在");
        }

        if (task.getStatus() != TaskStatus.COMPLETED) {
            return Map.of("success", false, "message", "任务尚未完成");
        }

        return Map.of(
                "success", true,
                "taskId", task.getTaskId(),
                "query", task.getQuery(),
                "result", task.getResult(),
                "completedAt", task.getCompletedAt().toString());
    }

    /**
     * 研究任务实体
     */
    @lombok.Data
    public static class ResearchTask {
        private String taskId;
        private String query;
        private TaskStatus status;
        private String containerId;
        private List<String> steps;
        private String result;
        private String error;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
    }

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        CREATED, // 已创建
        RUNNING, // 执行中
        COMPLETED, // 已完成
        FAILED // 失败
    }
}
