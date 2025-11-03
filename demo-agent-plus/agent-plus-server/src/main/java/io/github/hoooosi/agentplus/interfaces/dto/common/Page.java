package io.github.hoooosi.agentplus.interfaces.dto.common;

import lombok.Data;

@Data
public class Page {
    private Integer page = 1;
    private Integer pageSize = 15;
}
