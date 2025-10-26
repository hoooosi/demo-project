package io.github.hoooosi.agentplus.domain.auth.constant;

import lombok.Getter;

@Getter
public enum FeatureType {
    LOGIN("LOGIN", "登录功能"), REGISTER("REGISTER", "注册功能");

    private final String code;
    private final String name;

    FeatureType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static FeatureType fromCode(String code) {
        for (FeatureType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown feature type: " + code);
    }
}
