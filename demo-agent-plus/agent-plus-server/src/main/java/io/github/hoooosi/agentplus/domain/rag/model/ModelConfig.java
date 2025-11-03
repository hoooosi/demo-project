package io.github.hoooosi.agentplus.domain.rag.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.hoooosi.agentplus.domain.llm.model.enums.ModelType;
import io.github.hoooosi.agentplus.infrastructure.llm.protocol.enums.ProviderProtocol;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/** RAG模型配置 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class ModelConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** API密钥 */
    private String apiKey;
    /** API基础URL */
    private String baseUrl;
    private ModelType modelType;
    private ProviderProtocol protocol;
    private String modelEndpoint;

    @JsonIgnore
    public boolean isChatType() {
        return this.modelType == ModelType.CHAT;
    }
}
