package io.github.hoooosi.agentplus.domain.rag.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class RerankResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -4763176490538778562L;
    /** 唯一标识符 */
    private String id;
    /** 搜索结果列表 */
    private List<SearchResult> results;
    /** 令牌统计信息 */
    private Meta meta;

    /** 搜索结果项 */
    @Data
    public static class SearchResult implements Serializable {

        @Serial
        private static final long serialVersionUID = -2428070945016880585L;
        /** 文档内容 */
        private Document document;
        /** 索引位置 */
        private Integer index;
        /** 相关性分数 */
        @JsonProperty("relevance_score")
        private Double relevanceScore;
    }

    /** 文档内容 */
    @Data
    public static class Document implements Serializable {

        @Serial
        private static final long serialVersionUID = -6132815214174496256L;
        /** 文本内容 */
        private String text;
    }

    /** 令牌统计信息 */
    @Data
    public static class Meta implements Serializable {

        @Serial
        private static final long serialVersionUID = 5986231625205198272L;
        private TokenInfo tokens;
        @JsonProperty("billed_units")
        private BilledUnits billedUnits;
    }

    @Data
    public static class BilledUnits implements Serializable {

        @Serial
        private static final long serialVersionUID = 5723230611565604949L;
        @JsonProperty("input_tokens")
        private Integer inputTokens;
        @JsonProperty("output_tokens")
        private Integer outputTokens;
        @JsonProperty("output_units")
        private Integer outputUnits;
        private Integer classifications;
    }

    @Data
    public static class TokenInfo implements Serializable {

        @Serial
        private static final long serialVersionUID = -1766061142376465518L;
        /** 输入令牌数 */
        @JsonProperty("input_tokens")
        private Integer inputTokens;
        /** 输出令牌数 */
        @JsonProperty("output_tokens")
        private Integer outputTokens;
    }
}
