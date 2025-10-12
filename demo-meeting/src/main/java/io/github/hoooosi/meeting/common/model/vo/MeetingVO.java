package io.github.hoooosi.meeting.common.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会议 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingVO {
    private Long meetingId;
    private String meetingNo;
    private String meetingName;
    private Long startTime;
    private Long endTime;
    private Integer joinType;
    private Long creator;
    private Long createTime;
    private Integer myRole;
    private Integer myStatus;
    private Integer memberCount;
}

