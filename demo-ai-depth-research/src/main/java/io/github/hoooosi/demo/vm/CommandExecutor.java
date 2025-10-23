package io.github.hoooosi.demo.vm;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 命令执行器
 * 
 * 原理说明：
 * 1. 使用 Docker Exec API 在运行的容器中执行命令
 * 2. 异步执行，通过 CompletableFuture 返回结果
 * 3. 实时回调，将命令输出通过 WebSocket 推送到前端
 * 4. 区分标准输出（stdout）和错误输出（stderr）
 */
@Slf4j
@Component
@AllArgsConstructor
public class CommandExecutor {

    private final VirtualMachineManager vmManager;

    /**
     * 在虚拟机中执行命令
     * 
     * 原理：
     * 1. 使用 Docker Exec API 创建执行任务
     * 2. 启动异步回调监听输出流
     * 3. 实时捕获 stdout 和 stderr
     * 4. 通过 OutputCallback 将输出推送到前端
     * 
     * @param command  要执行的命令
     * @param callback 输出回调接口
     * @return CompletableFuture<ExecutionResult> 异步执行结果
     */
    public CompletableFuture<ExecutionResult> executeCommand(String command, OutputCallback callback) {
        CompletableFuture<ExecutionResult> future = new CompletableFuture<>();

        String containerId = vmManager.getCurrentContainerId();
        if (containerId == null) {
            future.completeExceptionally(new IllegalStateException("虚拟机未启动，无法执行命令"));
            return future;
        }

        DockerClient dockerClient = vmManager.getDockerClient();

        try {
            log.info("开始执行命令: {}", command);

            // 1. 创建 Exec 命令
            ExecCreateCmdResponse execCreateCmd = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true) // 附加标准输出
                    .withAttachStderr(true) // 附加错误输出
                    .withTty(false) // 不使用 TTY（以便区分 stdout/stderr）
                    .withCmd("/bin/bash", "-c", command) // 使用 bash 执行命令
                    .exec();

            String execId = execCreateCmd.getId();

            // 用于收集完整输出
            StringBuilder stdoutBuilder = new StringBuilder();
            StringBuilder stderrBuilder = new StringBuilder();

            // 2. 启动 Exec 并监听输出
            dockerClient.execStartCmd(execId)
                    .exec(new ResultCallback.Adapter<Frame>() {

                        @Override
                        public void onNext(Frame frame) {
                            // 解析输出帧
                            String text = new String(frame.getPayload(), StandardCharsets.UTF_8);
                            StreamType streamType = frame.getStreamType();

                            if (streamType == StreamType.STDOUT) {
                                // 标准输出
                                stdoutBuilder.append(text);
                                callback.onStdout(text);
                            } else if (streamType == StreamType.STDERR) {
                                // 错误输出
                                stderrBuilder.append(text);
                                callback.onStderr(text);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            log.error("命令执行过程中发生错误", throwable);
                            future.completeExceptionally(throwable);
                        }

                        @Override
                        public void onComplete() {
                            try {
                                // 3. 获取退出码
                                Integer exitCode = dockerClient.inspectExecCmd(execId)
                                        .exec()
                                        .getExitCodeLong()
                                        .intValue();

                                ExecutionResult result = ExecutionResult.builder()
                                        .command(command)
                                        .stdout(stdoutBuilder.toString())
                                        .stderr(stderrBuilder.toString())
                                        .exitCode(exitCode)
                                        .success(exitCode == 0)
                                        .build();

                                log.info("命令执行完成: exitCode={}", exitCode);
                                callback.onComplete(result);
                                future.complete(result);

                            } catch (Exception e) {
                                log.error("获取命令执行结果失败", e);
                                future.completeExceptionally(e);
                            }
                        }
                    })
                    .awaitCompletion(60, TimeUnit.SECONDS); // 最多等待 60 秒

        } catch (Exception e) {
            log.error("执行命令失败", e);
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * 输出回调接口
     * 用于实时接收命令执行的输出，并通过 WebSocket 推送到前端
     */
    @FunctionalInterface
    public interface OutputCallback {
        /**
         * 标准输出回调
         */
        void onStdout(String output);

        /**
         * 错误输出回调
         */
        default void onStderr(String error) {
            log.warn("命令错误输出: {}", error);
        }

        /**
         * 执行完成回调
         */
        default void onComplete(ExecutionResult result) {
            log.info("命令执行完成: {}", result.getCommand());
        }
    }
}
