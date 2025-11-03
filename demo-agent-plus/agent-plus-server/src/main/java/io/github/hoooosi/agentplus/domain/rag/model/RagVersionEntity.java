package io.github.hoooosi.agentplus.domain.rag.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.infrastructure.converter.ListStringConverter;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** RAG版本实体（完整快照） */
@EqualsAndHashCode(callSuper = true)
@TableName("rag_versions")
@Data
public class RagVersionEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;

    /** 版本ID */
    @TableId(type = IdType.ASSIGN_UUID)
    private Long id;
    /** 快照时的名称 */
    private String name;
    /** 快照时的图标 */
    private String icon;
    /** 快照时的描述 */
    private String description;
    /** 创建者ID */
    private String userId;
    /** 版本号 (如 "1.0.0") */
    private String version;
    /** 更新日志 */
    private String changeLog;
    /** 标签列表 */
    @TableField(value = "labels", typeHandler = ListStringConverter.class)
    private List<String> labels;
    /** 原始RAG数据集ID（仅标识用） */
    private String originalRagId;
    /** 原始RAG名称（快照时） */
    private String originalRagName;
    /** 文件数量 */
    private Integer fileCount;
    /** 总大小（字节） */
    private Long totalSize;
    /** 文档单元数量 */
    private Integer documentCount;
    /** 发布状态：1:审核中, 2:已发布, 3:拒绝, 4:已下架 */
    private Integer publishStatus;
    /** 审核拒绝原因 */
    private String rejectReason;
    /** 审核时间 */
    private Long reviewTime;
    /** 发布时间 */
    private Long publishedAt;
}