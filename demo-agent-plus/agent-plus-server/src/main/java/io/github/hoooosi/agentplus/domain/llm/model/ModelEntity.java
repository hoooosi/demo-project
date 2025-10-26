package io.github.hoooosi.agentplus.domain.llm.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.domain.llm.enums.ModelType;
import io.github.hoooosi.agentplus.infrastructure.converter.ModelTypeConverter;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

/** 模型领域模型 */
@EqualsAndHashCode(callSuper = true)
@TableName("models")
@Data
@Accessors(chain = true)
public class ModelEntity extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long providerId;
    private String modelId;
    private String name;
    private String description;
    /** 模型部署名称 */
    private String modelEndpoint;
    private Boolean isOfficial;
    @TableField(typeHandler = ModelTypeConverter.class, jdbcType = JdbcType.VARCHAR)
    private ModelType type;
    private Boolean status;
}
