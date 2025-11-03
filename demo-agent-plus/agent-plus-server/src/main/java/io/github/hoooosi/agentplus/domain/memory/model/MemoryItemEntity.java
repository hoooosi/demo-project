package io.github.hoooosi.agentplus.domain.memory.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.infrastructure.converter.ListStringConverter;
import io.github.hoooosi.agentplus.infrastructure.converter.MapConverter;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/** 记忆条目实体（memory_items） */
@EqualsAndHashCode(callSuper = true)
@TableName("memory_items")
@Data
public class MemoryItemEntity extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("type")
    private String type; // 使用字符串存储，取值见 MemoryType

    @TableField("text")
    private String text;

    @TableField(value = "data", typeHandler = MapConverter.class)
    private Map<String, Object> data;

    @TableField("importance")
    private Float importance;

    @TableField(value = "tags", typeHandler = ListStringConverter.class)
    private List<String> tags;

    @TableField("source_session_id")
    private String sourceSessionId;

    @TableField("dedupe_hash")
    private String dedupeHash;

    @TableField("status")
    private Integer status; // 1=active, 0=archived/deleted
}
