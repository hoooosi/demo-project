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
}

