package io.github.hoooosi.agentplus.domain.sso.model;

import lombok.Data;

@Data
public class SsoUserInfo {
    private String id;
    private String name;
    private String email;
    private String avatar;
    private String desc;
    private SsoProvider provider;
}
