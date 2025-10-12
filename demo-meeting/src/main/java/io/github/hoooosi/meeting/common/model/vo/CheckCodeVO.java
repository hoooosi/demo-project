package io.github.hoooosi.meeting.common.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckCodeVO {
    private String key;
    private String base64;
}
