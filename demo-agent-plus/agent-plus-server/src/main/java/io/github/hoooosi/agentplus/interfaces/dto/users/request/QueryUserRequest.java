package io.github.hoooosi.agentplus.interfaces.dto.users.request;


import io.github.hoooosi.agentplus.interfaces.dto.common.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QueryUserRequest extends Page {
    private String keyword;
}
