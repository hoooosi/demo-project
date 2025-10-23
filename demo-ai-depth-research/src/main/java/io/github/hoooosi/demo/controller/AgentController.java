package io.github.hoooosi.demo.controller;

import io.github.hoooosi.demo.service.AgentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Agent 研究控制器
 * 用户提交研究任务，Agent 自主在虚拟机中收集信息并整合
 */
@RestController
@RequestMapping("/api/agent")
@AllArgsConstructor
public class AgentController {

    private final AgentService agentService;

    /**
     * 提交研究任务
     * Agent 会自动创建虚拟机、执行搜索、分析信息、整合结果
     * 
     * @param request 研究任务请求
     * @return 任务ID，用于连接 WebSocket 查看实时进度
     */
    @PostMapping("/research")
    public Map<String, Object> submitResearchTask(@RequestBody ResearchRequest request) {
        try {
            String taskId = agentService.startResearchTask(request.query());
            return Map.of(
                    "success", true,
                    "taskId", taskId,
                    "message", "研究任务已创建，Agent 正在工作中...",
                    "websocketUrl", "ws://localhost:8081/ws/agent?taskId=" + taskId);
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "创建任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务状态
     */
    @GetMapping("/research/{taskId}")
    public Map<String, Object> getTaskStatus(@PathVariable String taskId) {
        return agentService.getTaskStatus(taskId);
    }

    /**
     * 获取任务结果
     */
    @GetMapping("/research/{taskId}/result")
    public Map<String, Object> getTaskResult(@PathVariable String taskId) {
        return agentService.getTaskResult(taskId);
    }

    public record ResearchRequest(String query) {
    }
}
