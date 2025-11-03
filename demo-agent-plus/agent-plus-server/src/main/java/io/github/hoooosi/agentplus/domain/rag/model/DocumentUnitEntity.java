package io.github.hoooosi.agentplus.domain.rag.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@TableName("document_unit")
@Data
public class DocumentUnitEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 7001509997040094844L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 文档ID */
    private Long fileId;
    /** 页码 */
    private Integer page;
    /** 当前页内容 */
    private String content;
    /** 是否进行向量化 */
    private Boolean isVector;
    /** ocr识别状态 */
    private Boolean isOcr;
    /** 相似度分数（非持久化字段，用于RAG搜索结果） */
    @TableField(exist = false)
    private Double similarityScore;
}
