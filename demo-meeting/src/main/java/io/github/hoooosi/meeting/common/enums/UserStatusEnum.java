package io.github.hoooosi.meeting.common.enums;

import lombok.Getter;

@Getter
public enum UserStatusEnum {
    DISABLE(0),
    ENABLE(1);

    private final Integer value;

    UserStatusEnum(Integer value) {
        this.value = value;
    }
}
