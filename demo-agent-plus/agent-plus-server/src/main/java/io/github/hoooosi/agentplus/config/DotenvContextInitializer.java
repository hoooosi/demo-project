package io.github.hoooosi.agentplus.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            // 加载 .env 文件
            Dotenv dotenv = Dotenv.load();

            // 将所有加载的变量放入一个 Map
            Map<String, Object> dotenvMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                // 将 .env 中的键值对添加到 Map
                dotenvMap.put(entry.getKey(), entry.getValue());
            });

            // 获取 Spring 环境配置
            ConfigurableEnvironment environment = applicationContext.getEnvironment();

            // 将 Map 包装成 PropertySource，并添加到 Spring 环境配置的最前面
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", dotenvMap));

        } catch (Exception e) {
            System.err.println("Could not load .env file: " + e.getMessage());
        }
    }
}