package io.github.hoooosi.agentplus.infrastructure.initializer;

import io.github.hoooosi.agentplus.domain.user.model.UserEntity;
import io.github.hoooosi.agentplus.domain.user.service.UserDomainService;
import io.github.hoooosi.agentplus.infrastructure.utils.PasswordUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** 默认数据初始化器 在应用启动时自动初始化默认用户数据 */
@Slf4j
@Component
@Order(100) // 确保在其他初始化器之后执行
@AllArgsConstructor
public class DefaultDataInitializer implements ApplicationRunner {
    private final UserDomainService userDomainService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始初始化默认数据...");
        try {
            initializeDefaultUsers();
            log.info("AgentX默认数据初始化完成！");
        } catch (Exception e) {
            log.error("AgentX默认数据初始化失败", e);
            // 不抛出异常，避免影响应用启动
        }
    }

    /** 初始化默认用户 */
    private void initializeDefaultUsers() {
        log.info("正在初始化默认用户...");

        // 初始化管理员用户
        initializeAdminUser();

        // 初始化测试用户
        initializeTestUser();
    }

    /** 初始化管理员用户 */
    private void initializeAdminUser() {
        // 优先使用环境变量配置，如果不存在则使用默认配置
        String adminEmail = "admin@admin.com";
        String adminPassword = "admin123";
        String adminNickname = "admin";

        try {
            // 检查管理员用户是否已存在
            UserEntity existingAdmin = userDomainService.findUserByAccount(adminEmail);
            if (existingAdmin != null) {
                log.info("管理员用户已存在，跳过初始化: {}", adminEmail);
                return;
            }

            // 创建管理员用户
            UserEntity adminUser = new UserEntity()
                    .setId(1L)
                    .setNickname(adminNickname)
                    .setEmail(adminEmail)
                    .setPhone("")
                    .setPassword(PasswordUtils.encode(adminPassword))
                    .setIsAdmin(true);

            // 直接插入，绕过业务校验（因为是系统初始化）
            userDomainService.createDefaultUser(adminUser);

            log.info("管理员用户初始化成功: {} (昵称: {})", adminEmail, adminNickname);
        } catch (Exception e) {
            log.error("管理员用户初始化失败: {}", adminEmail, e);
        }
    }

    /** 初始化测试用户 */
    private void initializeTestUser() {
        String testEmail = "test@test.com";
        String testPassword = "testtest";
        String testNickname = "test";

        try {
            // 检查测试用户是否已存在
            UserEntity existingTest = userDomainService.findUserByAccount(testEmail);
            if (existingTest != null) {
                log.info("测试用户已存在，跳过初始化: {}", testEmail);
                return;
            }

            // 创建测试用户
            UserEntity testUser = new UserEntity()
                    .setId(2L)
                    .setNickname(testNickname)
                    .setEmail(testEmail)
                    .setPhone("")
                    .setPassword(PasswordUtils.encode(testPassword))
                    .setIsAdmin(false);

            // 直接插入，绕过业务校验（因为是系统初始化）
            userDomainService.createDefaultUser(testUser);
            log.info("测试用户初始化成功: {} (昵称: {})", testEmail, testNickname);
        } catch (Exception e) {
            log.error("测试用户初始化失败: {}", testEmail, e);
        }
    }
}
