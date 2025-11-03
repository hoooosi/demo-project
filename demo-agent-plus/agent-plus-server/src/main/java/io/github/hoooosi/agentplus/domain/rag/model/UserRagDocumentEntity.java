package io.github.hoooosi.agentplus.domain.rag.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/** 用户RAG文档快照实体 */
@TableName("user_rag_documents")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserRagDocumentEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;

    /** 文档快照ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 关联的用户RAG ID */
    private String userRagId;
    /** 关联的用户RAG文件ID */
    private String userRagFileId;
    /** 原始文档单元ID（仅标识） */
    private String originalDocumentId;
    /** 文档内容 */
    private String content;
    /** 页码 */
    private Integer page;
    /** 向量ID（在向量数据库中的ID） */
    private String vectorId;
}