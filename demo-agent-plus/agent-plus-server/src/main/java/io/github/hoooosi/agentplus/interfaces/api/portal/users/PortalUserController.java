package io.github.hoooosi.agentplus.interfaces.api.portal.users;

import io.github.hoooosi.agentplus.application.llm.dto.ModelDTO;
import io.github.hoooosi.agentplus.application.llm.service.LLMAppService;
import io.github.hoooosi.agentplus.application.user.dto.UserDTO;
import io.github.hoooosi.agentplus.application.user.dto.UserSettingsDTO;
import io.github.hoooosi.agentplus.application.user.service.UserAppService;
import io.github.hoooosi.agentplus.application.user.service.UserSettingsAppService;
import io.github.hoooosi.agentplus.domain.llm.model.enums.ModelType;
import io.github.hoooosi.agentplus.domain.llm.model.enums.ProviderType;
import io.github.hoooosi.agentplus.infrastructure.auth.UserContext;
import io.github.hoooosi.agentplus.interfaces.api.common.Result;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.ChangePasswordRequest;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.UserSettingsUpdateRequest;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.UserUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class PortalUserController {

    private final UserAppService userAppService;
    private final UserSettingsAppService userSettingsAppService;
    private final LLMAppService llmAppService;

    /** 获取用户信息 */
    @GetMapping
    public Result<UserDTO> getUserInfo() {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(userAppService.getUserInfo(userId));
    }

    /** 修改用户信息 */
    @PostMapping
    public Result<?> updateUserInfo(@RequestBody @Validated UserUpdateRequest request) {
        Long userId = UserContext.getCurrentUserId();
        userAppService.updateUserInfo(request, userId);
        return Result.success();
    }

    /** 修改密码 */
    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody @Validated ChangePasswordRequest request) {
        Long userId = UserContext.getCurrentUserId();
        userAppService.changePassword(request, userId);
        return Result.success().message("密码修改成功");
    }

    /** 获取用户设置 */
    @GetMapping("/settings")
    public Result<UserSettingsDTO> getUserSettings() {
        Long userId = UserContext.getCurrentUserId();
        UserSettingsDTO settings = userSettingsAppService.getUserSettings(userId);
        return Result.success(settings);
    }

    /** 更新用户设置 */
    @PutMapping("/settings")
    public Result<UserSettingsDTO> updateUserSettings(@RequestBody @Validated UserSettingsUpdateRequest request) {
        Long userId = UserContext.getCurrentUserId();
        UserSettingsDTO settings = userSettingsAppService.updateUserSettings(request, userId);
        return Result.success(settings);
    }

    /** 获取用户默认模型ID */
    @GetMapping("/settings/default-model")
    public Result<Long> getUserDefaultModelId() {
        Long userId = UserContext.getCurrentUserId();
        Long defaultModelId = userSettingsAppService.getUserDefaultModelId(userId);
        return Result.success(defaultModelId);
    }

    /** 获取可用的OCR模型列表（复用现有模型接口，支持视觉模型） */
    @GetMapping("/settings/ocr-models")
    public Result<List<ModelDTO>> getOcrModels() {
        Long userId = UserContext.getCurrentUserId();
        // OCR模型实际上是对话模型，但支持视觉输入，所以复用CHAT类型
        List<ModelDTO> models = llmAppService.getActiveModelsByType(ProviderType.ALL, userId, ModelType.CHAT);
        return Result.success(models);
    }

    /** 获取可用的嵌入模型列表（按模型类型筛选） */
    @GetMapping("/settings/embedding-models")
    public Result<List<ModelDTO>> getEmbeddingModels() {
        Long userId = UserContext.getCurrentUserId();
        // 筛选嵌入模型类型
        List<ModelDTO> models = llmAppService.getActiveModelsByType(ProviderType.ALL, userId, ModelType.EMBEDDING);
        return Result.success(models);
    }
}
