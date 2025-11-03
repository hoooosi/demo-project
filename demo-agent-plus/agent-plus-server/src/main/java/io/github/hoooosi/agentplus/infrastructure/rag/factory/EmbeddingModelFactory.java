package io.github.hoooosi.agentplus.infrastructure.rag.factory;

import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/** 嵌入模型工厂类 根据用户配置动态创建嵌入模型实例 */
@Component
public class EmbeddingModelFactory {

    /** 嵌入模型配置类 */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class EmbeddingConfig {
        private String apiKey;
        private String baseUrl;
        private String modelEndpoint;
    }

    /** 根据配置创建OpenAI嵌入模型实例
     * @param config 嵌入模型配置
     * @return OpenAiEmbeddingModel实例 */
    public OpenAiEmbeddingModel createEmbeddingModel(EmbeddingConfig config) {
        return OpenAiEmbeddingModel.builder().apiKey(config.getApiKey()).baseUrl(config.getBaseUrl())
                .modelName(config.getModelEndpoint()).build();
    }
}