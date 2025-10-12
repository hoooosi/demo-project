package io.github.hoooosi.meeting.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@TableName(value = "tb_meeting_member")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class MeetingMember extends TimeField {
    private Long meetingId;
    private Long userId;
    private String nickName;
    private Integer role;
    private Integer status;
}

