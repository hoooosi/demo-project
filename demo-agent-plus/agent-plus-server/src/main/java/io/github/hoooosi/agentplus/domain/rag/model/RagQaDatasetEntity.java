package io.github.hoooosi.agentplus.domain.rag.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/** RAG知识库数据集实体 */
@EqualsAndHashCode(callSuper = true)
@TableName("ai_rag_qa_dataset")
@Data
public class RagQaDatasetEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -5803685552931418952L;

    /** 数据集ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 数据集名称 */
    private String name;
    /** 数据集图标 */
    private String icon;
    /** 数据集说明 */
    private String description;
    /** 用户ID */
    private String userId;
}
