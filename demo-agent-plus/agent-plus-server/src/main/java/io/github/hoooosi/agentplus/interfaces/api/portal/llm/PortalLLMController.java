package io.github.hoooosi.agentplus.interfaces.api.portal.llm;

import io.github.hoooosi.agentplus.application.llm.dto.ModelDTO;
import io.github.hoooosi.agentplus.application.llm.dto.ProviderDTO;
import io.github.hoooosi.agentplus.application.llm.service.LLMAppService;
import io.github.hoooosi.agentplus.domain.llm.model.enums.ModelType;
import io.github.hoooosi.agentplus.domain.llm.model.enums.ProviderType;
import io.github.hoooosi.agentplus.infrastructure.auth.UserContext;
import io.github.hoooosi.agentplus.infrastructure.llm.protocol.enums.ProviderProtocol;
import io.github.hoooosi.agentplus.interfaces.api.common.Result;
import io.github.hoooosi.agentplus.interfaces.dto.llm.request.ModelCreateRequest;
import io.github.hoooosi.agentplus.interfaces.dto.llm.request.ModelUpdateRequest;
import io.github.hoooosi.agentplus.interfaces.dto.llm.request.ProviderCreateRequest;
import io.github.hoooosi.agentplus.interfaces.dto.llm.request.ProviderUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/** 大模型服务商 */
@RestController
@RequestMapping("/llms")
@AllArgsConstructor
public class PortalLLMController {

    public final LLMAppService llmAppService;

    /** 获取服务商详细信息
     * @param providerId 服务商id */
    @GetMapping("/providers/{providerId}")
    public Result<ProviderDTO> getProviderDetail(@PathVariable Long providerId) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(llmAppService.getProviderDetail(providerId, userId));
    }

    /** 获取服务商列表，支持按类型过滤
     * @param type 服务商类型：all-所有，official-官方，user-用户的（默认）
     * @return 服务商列表 */
    @GetMapping("/providers")
    public Result<List<ProviderDTO>> getProviders(@RequestParam(required = false, defaultValue = "all") String type) {

        ProviderType providerType = ProviderType.fromCode(type);
        Long userId = UserContext.getCurrentUserId();
        return Result.success(llmAppService.getProvidersByType(providerType, userId));
    }

    /** 创建服务提供商 */
    @PostMapping("/providers")
    public Result<ProviderDTO> createProvider(@RequestBody ProviderCreateRequest providerCreateRequest) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(llmAppService.createProvider(providerCreateRequest, userId));
    }

    /** 更新服务提供商 */
    @PutMapping("/providers")
    public Result<ProviderDTO> updateProvider(@RequestBody ProviderUpdateRequest providerUpdateRequest) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(llmAppService.updateProvider(providerUpdateRequest, userId));
    }

    /** 修改服务商状态
     * @param providerId 服务商id */
    @PostMapping("/providers/{providerId}/status")
    public Result<Void> updateProviderStatus(@PathVariable Long providerId) {
        Long userId = UserContext.getCurrentUserId();
        llmAppService.updateProviderStatus(providerId, userId);
        return Result.success();
    }

    /** 删除服务提供商
     * @param providerId 服务提供商ID */
    @DeleteMapping("/providers/{providerId}")
    public Result<Void> deleteProvider(@PathVariable Long providerId) {
        Long userId = UserContext.getCurrentUserId();
        llmAppService.deleteProvider(providerId, userId);
        return Result.success();
    }

    /** 获取服务提供商列表 */
    @GetMapping("/providers/protocols")
    public Result<List<ProviderProtocol>> getProviders() {
        return Result.success(llmAppService.getUserProviderProtocols());
    }

    /** 添加模型 */
    @PostMapping("/models")
    public Result<ModelDTO> createModel(@RequestBody ModelCreateRequest modelCreateRequest) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(llmAppService.createModel(modelCreateRequest, userId));
    }

    /** 修改模型 */
    @PutMapping("/models")
    public Result<ModelDTO> updateModel(@RequestBody @Validated ModelUpdateRequest modelUpdateRequest) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(llmAppService.updateModel(modelUpdateRequest, userId));
    }

    /** 删除模型
     * @param modelId 模型主键 */
    @DeleteMapping("/models/{modelId}")
    public Result<Void> deleteModel(@PathVariable Long modelId) {
        Long userId = UserContext.getCurrentUserId();
        llmAppService.deleteModel(modelId, userId);
        return Result.success();
    }

    /** 修改模型状态
     * @param modelId 模型主键 */
    @PutMapping("/models/{modelId}/status")
    public Result<Void> updateModelStatus(@PathVariable Long modelId) {
        Long userId = UserContext.getCurrentUserId();
        llmAppService.updateModelStatus(modelId, userId);
        return Result.success();
    }

    /** 获取模型类型 */
    @GetMapping("/models/types")
    public Result<List<ModelType>> getModelTypes() {
        return Result.success(Arrays.asList(ModelType.values()));
    }

    /** 获取所有激活模型
     * @param modelType 模型类型（可选），不传则查询所有类型
     * @param official 是否只获取官方模型（可选），true-仅官方模型，false或不传-所有模型
     * @return 模型列表 */
    @GetMapping("/models")
    public Result<List<ModelDTO>> getModels(@RequestParam(required = false) String modelType,
                                            @RequestParam(required = false) Boolean official) {
        Long userId = UserContext.getCurrentUserId();
        ModelType type = modelType != null ? ModelType.fromCode(modelType) : null;
        ProviderType providerType = (official != null && official) ? ProviderType.OFFICIAL : ProviderType.ALL;
        return Result.success(llmAppService.getActiveModelsByType(providerType, userId, type));
    }

    /** 获取用户默认的模型详情 */
    @GetMapping("/models/default")
    public Result<ModelDTO> getDefaultModel() {
        Long userId = UserContext.getCurrentUserId();
        ModelDTO modelDTO = llmAppService.getDefaultModel(userId);
        return Result.success(modelDTO);
    }
}
