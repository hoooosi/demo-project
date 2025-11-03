package io.github.hoooosi.agentplus.domain.rag.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/** 此类为文档分片实体类，映射数据库表，不进行任何操作传参使用 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentChunkEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5264446804791048406L;
    /** id */
    @TableId(type = IdType.ASSIGN_ID)
    private Long embeddingId;
    /** 内容 */
    private String text;
    /** 元数据 */
    private String metadata;

}
