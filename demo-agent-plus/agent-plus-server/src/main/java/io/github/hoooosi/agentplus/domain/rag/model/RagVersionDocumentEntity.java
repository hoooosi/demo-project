package io.github.hoooosi.agentplus.domain.rag.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/** RAG版本文档单元实体（文档内容快照） */
@EqualsAndHashCode(callSuper = true)
@TableName("rag_version_documents")
@Data
public class RagVersionDocumentEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;
    /** 文档单元ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 关联的RAG版本ID */
    private Long ragVersionId;
    /** 关联的版本文件ID */
    private Long ragVersionFileId;
    /** 原始文档单元ID（仅标识） */
    private Long originalDocumentId;
    /** 文档内容 */
    private String content;
    /** 页码 */
    private Integer page;
    /** 向量ID（在向量数据库中的ID） */
    private String vectorId;
}