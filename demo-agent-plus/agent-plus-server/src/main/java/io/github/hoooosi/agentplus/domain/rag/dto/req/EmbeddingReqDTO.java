package io.github.hoooosi.agentplus.domain.rag.dto.req;


import lombok.Data;

@Data
public class EmbeddingReqDTO {

    /** 模型 */
    private String model;
    /** 语料 */
    private String input;
    private String encoding_format = "float";
}
