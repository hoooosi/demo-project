package io.github.hoooosi.meeting.common.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class TokenDTO implements Serializable {
    private String tokenKey;
    private Long userId;
    private Long roomId;
}
