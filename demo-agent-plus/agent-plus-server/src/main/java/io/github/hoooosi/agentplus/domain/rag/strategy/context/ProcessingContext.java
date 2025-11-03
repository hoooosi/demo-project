package io.github.hoooosi.agentplus.domain.rag.strategy.context;

import io.github.hoooosi.agentplus.domain.rag.message.RagDocMessage;
import io.github.hoooosi.agentplus.domain.rag.model.ModelConfig;
import io.github.hoooosi.agentplus.infrastructure.llm.config.ProviderConfig;
import io.github.hoooosi.agentplus.infrastructure.llm.protocol.enums.ProviderProtocol;
import io.github.hoooosi.agentplus.infrastructure.rag.factory.EmbeddingModelFactory;
import io.github.hoooosi.agentplus.infrastructure.rag.service.UserModelConfigResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Markdown处理上下文 */
@AllArgsConstructor
@Data
public class ProcessingContext {

    private static final Logger log = LoggerFactory.getLogger(ProcessingContext.class);

    /** 嵌入模型配置 */
    private final EmbeddingModelFactory.EmbeddingConfig embeddingConfig;
    /** LLM配置（用于公式和表格翻译） */
    private final ProviderConfig llmConfig;
    /** 视觉模型配置（用于图片处理） */
    private final ProviderConfig visionModelConfig;
    /** 用户ID */
    private final Long userId;
    /** 文件ID */
    private final Long fileId;


    /** 从RagDocSyncOcrMessage构建处理上下文
     * @param message 消息对象
     * @param userModelConfigResolver 用户模型配置解析器
     * @return 处理上下文 */
    public static ProcessingContext from(RagDocMessage message, UserModelConfigResolver userModelConfigResolver) {
        try {
            Long userId = message.getUserId();

            // 获取嵌入模型配置
            EmbeddingModelFactory.EmbeddingConfig embeddingConfig = null;
            try {
                ModelConfig embeddingModelConfig = userModelConfigResolver.getUserEmbeddingModelConfig(userId);
                embeddingConfig = new EmbeddingModelFactory.EmbeddingConfig(embeddingModelConfig.getApiKey(),
                        embeddingModelConfig.getBaseUrl(), embeddingModelConfig.getModelEndpoint());
            } catch (Exception e) {
                log.warn("获取用户 {} 嵌入模型配置失败: {}", userId, e.getMessage());
            }

            // 获取聊天模型配置（用于LLM处理）
            ProviderConfig llmConfig = null;
            try {
                ModelConfig chatModelConfig = userModelConfigResolver.getUserChatModelConfig(userId);
                llmConfig = new ProviderConfig(chatModelConfig.getApiKey(), chatModelConfig.getBaseUrl(),
                        chatModelConfig.getModelEndpoint(), ProviderProtocol.OPENAI);
            } catch (Exception e) {
                log.warn("获取用户 {} 聊天模型配置失败: {}", userId, e.getMessage());
            }

            // 获取OCR/视觉模型配置
            ProviderConfig visionModelConfig = null;
            try {
                ModelConfig ocrModelConfig = userModelConfigResolver.getUserOcrModelConfig(userId);
                visionModelConfig = new ProviderConfig(ocrModelConfig.getApiKey(), ocrModelConfig.getBaseUrl(),
                        ocrModelConfig.getModelEndpoint(), ProviderProtocol.OPENAI);
            } catch (Exception e) {
                log.warn("获取用户 {} OCR模型配置失败: {}", userId, e.getMessage());
            }

            return new ProcessingContext(embeddingConfig, llmConfig, visionModelConfig, userId, message.getFileId());

        } catch (Exception e) {
            log.error("从消息创建ProcessingContext失败: {}", e.getMessage(), e);
            // 创建一个空配置的上下文作为回退
            return new ProcessingContext(null, null, null, message.getUserId(), message.getFileId());
        }
    }
}