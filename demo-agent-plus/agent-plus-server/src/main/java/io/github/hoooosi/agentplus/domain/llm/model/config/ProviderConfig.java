package io.github.hoooosi.agentplus.domain.llm.model.config;

import lombok.Data;

import java.io.Serializable;

/** 服务商配置 */
@Data
public class ProviderConfig implements Serializable {
    private String apiKey;
    private String baseUrl;
}