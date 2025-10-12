package io.github.hoooosi.meeting.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@TableName(value = "tb_meeting")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class Meeting extends TimeField {
    @TableId(value = "meeting_id", type = IdType.ASSIGN_ID)
    private Long meetingId;
    private String meetingNo;
    private String meetingName;
    private Long startTime;
    private Long endTime;
    private Integer joinType;
    private String joinPass;
    private Long creator;
}

