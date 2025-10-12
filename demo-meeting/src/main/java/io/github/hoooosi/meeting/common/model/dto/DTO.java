package io.github.hoooosi.meeting.common.model.dto;

public class DTO {
    public record PagingRequest(
            Integer pageNum,
            Integer pageSize
    ) {
        public PagingRequest {
            if (pageNum == null || pageNum < 1) {
                pageNum = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 10;
            }
        }
    }
}
