package io.github.hoooosi.agentplus.application.memory.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 记忆条目 DTO */
@Data
public class MemoryItemDTO {
    private Long  id;
    private String type;
    private String text;
    private Float importance;
    private List<String> tags;
    private Long  createdAt;
    private Long  updatedAt;
}
