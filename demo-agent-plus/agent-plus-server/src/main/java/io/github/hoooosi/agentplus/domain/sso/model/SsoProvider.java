package io.github.hoooosi.agentplus.domain.sso.model;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
public enum SsoProvider {
    COMMUNITY("community", "敲鸭"),
    GITHUB("github", "GitHub"),
    GOOGLE("google", "Google"),
    WECHAT("wechat", "微信");

    private final String code;
    private final String name;

    SsoProvider(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SsoProvider fromCode(String code) {
        for (SsoProvider provider : values()) {
            if (provider.code.equals(code)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown SSO provider: " + code);
    }
}