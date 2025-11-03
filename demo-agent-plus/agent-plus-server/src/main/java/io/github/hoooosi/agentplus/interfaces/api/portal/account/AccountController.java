package io.github.hoooosi.agentplus.interfaces.api.portal.account;

import io.github.hoooosi.agentplus.application.account.service.AccountAppService;
import io.github.hoooosi.agentplus.application.account.dto.AccountDTO;
import io.github.hoooosi.agentplus.infrastructure.auth.UserContext;
import io.github.hoooosi.agentplus.interfaces.api.common.Result;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 账户管理控制层 提供用户账户管理的API接口 */
@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountAppService accountAppService;

    /** 获取当前用户账户信息 */
    @GetMapping("/current")
    public Result<AccountDTO> getCurrentUserAccount() {
        Long userId = UserContext.getCurrentUserId();
        AccountDTO account = accountAppService.getUserAccount(userId);
        return Result.success(account);
    }
}
