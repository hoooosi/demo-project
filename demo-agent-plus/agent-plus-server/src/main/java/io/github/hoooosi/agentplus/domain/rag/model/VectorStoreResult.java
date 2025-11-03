package io.github.hoooosi.agentplus.domain.rag.model;

import io.github.hoooosi.agentplus.domain.rag.constant.SearchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/** vector_store表查询结果统一数据结构 同时支持向量检索和关键词检索的结果表示 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VectorStoreResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 向量存储ID */
    private String embeddingId;
    /** 文本内容 */
    private String text;
    /** 元数据（JSON格式） */
    private Map<String, Object> metadata;
    /** 检索分数（向量相似度分数或关键词rank分数） */
    private Double score;
    /** 检索类型标识 */
    private SearchType searchType;

    /** 从metadata中获取DOCUMENT_ID
     * @return 文档ID */
    public String getDocumentId() {
        if (metadata == null) {
            return null;
        }
        return (String) metadata.get("DOCUMENT_ID");
    }

    /** 从metadata中获取FILE_ID
     * @return 文件ID */
    public String getFileId() {
        if (metadata == null) {
            return null;
        }
        return (String) metadata.get("FILE_ID");
    }

    /** 从metadata中获取DATA_SET_ID
     * @return 数据集ID */
    public String getDataSetId() {
        if (metadata == null) {
            return null;
        }
        return (String) metadata.get("DATA_SET_ID");
    }

    /** 从metadata中获取FILE_NAME
     * @return 文件名 */
    public String getFileName() {
        if (metadata == null) {
            return null;
        }
        return (String) metadata.get("FILE_NAME");
    }
}