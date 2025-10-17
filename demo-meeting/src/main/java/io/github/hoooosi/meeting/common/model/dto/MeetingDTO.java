package io.github.hoooosi.meeting.common.model.dto;

/**
 * 会议相关的 DTO
 */
public class MeetingDTO {

    public record LoadMeetingDTO(
            DTO.PagingRequest paging,
            String keyword
    ) {
    }

    public record QuickStartMeetingDTO(
            String password,
            String meetingName
    ) {
    }

    public record PreJoinMeetingDTO(
            Long meetingId,
            String password
    ) {
    }

    public record JoinMeetingDTO(
            Long meetingId,
            String password,
            String nickName
    ) {
    }
}

