package io.github.hoooosi.agentplus.application.memory.assembler;


import io.github.hoooosi.agentplus.application.memory.dto.MemoryItemDTO;
import io.github.hoooosi.agentplus.domain.memory.model.MemoryItemEntity;

import java.util.List;
import java.util.stream.Collectors;

public class MemoryAssembler {

    public static MemoryItemDTO toDTO(MemoryItemEntity e) {
        MemoryItemDTO dto = new MemoryItemDTO();
        dto.setId(e.getId());
        dto.setType(e.getType());
        dto.setText(e.getText());
        dto.setImportance(e.getImportance());
        dto.setTags(e.getTags());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    public static List<MemoryItemDTO> toDTOs(List<MemoryItemEntity> list) {
        return list.stream().map(MemoryAssembler::toDTO).collect(Collectors.toList());
    }
}
