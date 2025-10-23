package io.github.hoooosi.demo.vm;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class VirtualMachineManager {

    private DockerClient dockerClient;

    // 虚拟机状态：是否已被占用
    private final AtomicBoolean vmOccupied = new AtomicBoolean(false);

    // 当前虚拟机容器ID
    private final AtomicReference<String> currentContainerId = new AtomicReference<>(null); // 虚拟机镜像名称（使用自定义镜像）
    private static final String VM_IMAGE = "agent-workspace:latest";

    // 备用镜像（如果自定义镜像不存在）
    private static final String FALLBACK_IMAGE = "ubuntu:22.04";

    // 容器名称
    private static final String CONTAINER_NAME = "agent-vm-workspace";

    /**
     * 初始化 Docker 客户端
     * 原理：配置 Docker 连接参数，支持本地 Docker Desktop 或远程 Docker 服务
     */
    @PostConstruct
    public void init() {
        try {
            // 配置 Docker 客户端
            DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost("tcp://192.168.31.3:2375")
                    .build();

            // 创建 HTTP 客户端
            DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost())
                    .sslConfig(config.getSSLConfig())
                    .maxConnections(100)
                    .connectionTimeout(Duration.ofSeconds(30))
                    .responseTimeout(Duration.ofSeconds(45))
                    .build();

            this.dockerClient = DockerClientBuilder.getInstance(config)
                    .withDockerHttpClient(httpClient)
                    .build();

            // 测试连接
            dockerClient.pingCmd().exec();
            log.info("Docker 客户端初始化成功");

            // 清理可能存在的旧容器
            cleanupExistingContainer();

        } catch (Exception e) {
            log.error("Docker 客户端初始化失败，请确保 Docker 服务已启动", e);
            throw new RuntimeException("无法连接到 Docker 服务，请检查 Docker 是否正在运行", e);
        }
    }

    /**
     * 清理可能存在的旧容器
     */
    private void cleanupExistingContainer() {
        try {
            dockerClient.inspectContainerCmd(CONTAINER_NAME).exec();
            log.info("发现旧容器，正在清理...");
            try {
                dockerClient.stopContainerCmd(CONTAINER_NAME).withTimeout(5).exec();
            } catch (Exception e) {
                log.debug("停止容器失败（容器可能已停止）");
            }
            dockerClient.removeContainerCmd(CONTAINER_NAME).withForce(true).exec();
            log.info("旧容器清理完成");
        } catch (NotFoundException e) {
            log.debug("未发现旧容器");
        } catch (Exception e) {
            log.warn("清理旧容器时发生错误", e);
        }
    }

    /**
     * 创建并启动虚拟机
     * <p>
     * 原理：
     * 1. 使用 CAS 操作检查是否已有虚拟机在运行（保证全局唯一性）
     * 2. 拉取 Ubuntu 镜像（如果本地不存在）
     * 3. 创建容器并配置：TTY、持久运行、资源限制
     * 4. 启动容器
     *
     * @return 容器ID
     * @throws VmAlreadyExistsException 如果虚拟机已被占用
     */
    public String createAndStartVm() {
        // CAS 操作：检查并设置占用标志
        if (!vmOccupied.compareAndSet(false, true)) {
            throw new VmAlreadyExistsException("虚拟机已被其他用户占用，请稍后再试");
        }

        try {
            log.info("开始创建虚拟机..."); // 1. 确保镜像存在
            ensureImageExists();

            // 获取要使用的镜像
            String imageToUse = getImageToUse();
            log.info("使用镜像: {}", imageToUse);

            // 2. 创建容器
            CreateContainerResponse container = dockerClient.createContainerCmd(imageToUse)
                    .withName(CONTAINER_NAME)
                    .withTty(true) // 启用 TTY（模拟终端）
                    .withStdinOpen(true) // 保持标准输入打开
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withCmd("/bin/bash", "-c", "tail -f /dev/null") // 保持容器运行
                    .withHostConfig(HostConfig.newHostConfig()
                            .withMemory(512 * 1024 * 1024L) // 限制内存 512MB
                            .withCpuQuota(50000L) // 限制 CPU 使用率 50%
                            .withAutoRemove(false)) // 不自动删除
                    .exec();

            String containerId = container.getId();
            currentContainerId.set(containerId);

            // 3. 启动容器
            dockerClient.startContainerCmd(containerId).exec();

            log.info("虚拟机创建成功: containerId={}", containerId);
            return containerId;

        } catch (Exception e) {
            // 失败时释放占用标志
            vmOccupied.set(false);
            currentContainerId.set(null);
            log.error("创建虚拟机失败", e);
            throw new RuntimeException("创建虚拟机失败: " + e.getMessage(), e);
        }
    }

    /**
     * 确保 Docker 镜像存在
     * 优先使用自定义镜像，如果不存在则尝试构建，失败则使用备用镜像
     */
    private void ensureImageExists() {
        try {
            dockerClient.inspectImageCmd(VM_IMAGE).exec();
            log.info("✅ 自定义镜像 {} 已存在", VM_IMAGE);
        } catch (NotFoundException e) {
            log.info("❌ 镜像 {} 不存在", VM_IMAGE);

            // 尝试从 Dockerfile 构建镜像
            if (tryBuildImage()) {
                log.info("✅ 自定义镜像构建成功");
                return;
            }

            // 构建失败，使用备用镜像
            log.warn("⚠️ 无法构建自定义镜像，将使用备用镜像: {}", FALLBACK_IMAGE);
            ensureFallbackImageExists();
        }
    }

    /**
     * 尝试构建自定义镜像
     */
    private boolean tryBuildImage() {
        try {
            // 检查 Dockerfile 是否存在
            java.io.File dockerfile = new java.io.File("Dockerfile");
            if (!dockerfile.exists()) {
                log.warn("Dockerfile 不存在，无法构建镜像");
                return false;
            }

            log.info("🔨 开始构建自定义镜像 {}...", VM_IMAGE);
            log.info("📝 这可能需要几分钟时间，请耐心等待...");

            dockerClient.buildImageCmd()
                    .withDockerfile(dockerfile)
                    .withTags(java.util.Set.of(VM_IMAGE))
                    .start()
                    .awaitImageId();

            log.info("✅ 镜像构建完成");
            return true;
        } catch (Exception ex) {
            log.error("❌ 镜像构建失败: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * 确保备用镜像存在
     */
    private void ensureFallbackImageExists() {
        try {
            dockerClient.inspectImageCmd(FALLBACK_IMAGE).exec();
            log.info("备用镜像 {} 已存在", FALLBACK_IMAGE);
        } catch (NotFoundException e) {
            log.info("开始拉取备用镜像 {}...", FALLBACK_IMAGE);
            try {
                dockerClient.pullImageCmd(FALLBACK_IMAGE)
                        .start()
                        .awaitCompletion();
                log.info("备用镜像拉取完成");
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("镜像拉取被中断", ie);
            }
        }
    }

    /**
     * 获取要使用的镜像名称
     */
    private String getImageToUse() {
        try {
            dockerClient.inspectImageCmd(VM_IMAGE).exec();
            return VM_IMAGE;
        } catch (NotFoundException e) {
            log.info("使用备用镜像: {}", FALLBACK_IMAGE);
            return FALLBACK_IMAGE;
        }
    }

    /**
     * 销毁虚拟机
     * <p>
     * 原理：
     * 1. 停止容器
     * 2. 删除容器
     * 3. 释放占用标志
     */
    public void destroyVm() {
        String containerId = currentContainerId.get();
        if (containerId == null) {
            log.warn("没有活动的虚拟机可销毁");
            return;
        }

        try {
            log.info("正在销毁虚拟机: containerId={}", containerId);

            // 停止容器（超时 5 秒）
            try {
                dockerClient.stopContainerCmd(containerId)
                        .withTimeout(5)
                        .exec();
            } catch (Exception e) {
                log.warn("停止容器失败（容器可能已停止）: {}", e.getMessage());
            }

            // 删除容器
            dockerClient.removeContainerCmd(containerId)
                    .withForce(true)
                    .exec();

            log.info("虚拟机销毁成功");

        } catch (Exception e) {
            log.error("销毁虚拟机时发生错误", e);
        } finally {
            // 无论成功失败都释放占用标志
            vmOccupied.set(false);
            currentContainerId.set(null);
        }
    }

    /**
     * 获取当前容器ID
     */
    public String getCurrentContainerId() {
        return currentContainerId.get();
    }

    /**
     * 检查虚拟机是否被占用
     */
    public boolean isVmOccupied() {
        return vmOccupied.get();
    }

    /**
     * 获取 Docker 客户端（供命令执行器使用）
     */
    public DockerClient getDockerClient() {
        return dockerClient;
    }

    /**
     * 应用关闭时清理资源
     */
    @PreDestroy
    public void cleanup() {
        log.info("应用关闭，开始清理虚拟机资源...");
        destroyVm();
        if (dockerClient != null) {
            try {
                dockerClient.close();
            } catch (Exception e) {
                log.warn("关闭 Docker 客户端时发生错误", e);
            }
        }
    }

    /**
     * 虚拟机已存在异常
     */
    public static class VmAlreadyExistsException extends RuntimeException {
        public VmAlreadyExistsException(String message) {
            super(message);
        }
    }
}
