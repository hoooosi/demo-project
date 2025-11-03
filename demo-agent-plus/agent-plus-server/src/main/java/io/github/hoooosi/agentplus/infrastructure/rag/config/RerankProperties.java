package io.github.hoooosi.agentplus.infrastructure.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rerank")
@Data
public class RerankProperties {
    /** 嵌入服务名称 */
    private String name;
    /** API密钥 */
    private String apiKey;
    /** API URL */
    private String apiUrl;
    /** 使用的模型名称 */
    private String model;
    /** 请求超时时间(毫秒) */
    private int timeout;
}
