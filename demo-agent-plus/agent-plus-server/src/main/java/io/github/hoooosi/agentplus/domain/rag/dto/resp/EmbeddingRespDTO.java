package io.github.hoooosi.agentplus.domain.rag.dto.resp;

import lombok.Data;

import java.util.ArrayList;

@Data
public class EmbeddingRespDTO {

    private String object;
    private ArrayList<EmbeddingDataBO> data;
    private String model;

    @Data
    public static class EmbeddingDataBO {
        private String object;
        private double[] embedding;
    }
}
