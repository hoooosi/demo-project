package io.github.hoooosi.agentplus.domain.rag.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/** RAG版本文件实体（文件快照） */
@TableName("rag_version_files")
@EqualsAndHashCode(callSuper = true)
@Data
public class RagVersionFileEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;
    /** 文件ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 关联的RAG版本ID */
    private Long ragVersionId;
    /** 原始文件ID（仅标识） */
    private Long originalFileId;
    /** 文件名 */
    private String fileName;
    /** 文件大小（字节） */
    private Long fileSize;
    /** 文件页数 */
    private Integer filePageSize;
    /** 文件类型 */
    private String fileType;
    /** 文件存储路径 */
    private String filePath;
    /** 处理状态 */
    private Integer processStatus;
    /** 向量化状态 */
    private Integer embeddingStatus;
}