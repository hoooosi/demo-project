package io.github.hoooosi.agentplus.application.llm.service;

import io.github.hoooosi.agentplus.application.llm.assembler.ModelAssembler;
import io.github.hoooosi.agentplus.application.llm.assembler.ProviderAssembler;
import io.github.hoooosi.agentplus.application.llm.dto.ModelDTO;
import io.github.hoooosi.agentplus.application.llm.dto.ProviderDTO;
import io.github.hoooosi.agentplus.domain.llm.enums.ModelType;
import io.github.hoooosi.agentplus.domain.llm.enums.ProviderType;
import io.github.hoooosi.agentplus.domain.llm.model.ModelEntity;
import io.github.hoooosi.agentplus.domain.llm.model.ProviderAggregate;
import io.github.hoooosi.agentplus.domain.llm.service.LLMDomainService;
import io.github.hoooosi.agentplus.domain.user.service.UserSettingsDomainService;
import io.github.hoooosi.agentplus.domain.user.service.model.ProviderEntity;
import io.github.hoooosi.agentplus.domain.user.service.protocol.ProviderProtocol;
import io.github.hoooosi.agentplus.infrastructure.entity.Operator;
import io.github.hoooosi.agentplus.interfaces.dto.users.llm.request.ModelCreateRequest;
import io.github.hoooosi.agentplus.interfaces.dto.users.llm.request.ModelUpdateRequest;
import io.github.hoooosi.agentplus.interfaces.dto.users.llm.request.ProviderCreateRequest;
import io.github.hoooosi.agentplus.interfaces.dto.users.llm.request.ProviderUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LLMAppService {
    private final LLMDomainService llmDomainService;
    private final UserSettingsDomainService userSettingsDomainService;

    /** 获取服务商聚合根
     * @param providerId 服务商id
     * @param userId 用户id
     * @return ProviderAggregate */
    public ProviderDTO getProviderDetail(Long providerId, Long userId) {
        ProviderAggregate providerAggregate = llmDomainService.getProviderAggregate(providerId, userId);
        return ProviderAssembler.toDTO(providerAggregate);
    }

    /** 创建服务商
     * @param providerCreateRequest 请求对象
     * @param userId 用户id
     * @return ProviderDTO */
    public ProviderDTO createProvider(ProviderCreateRequest providerCreateRequest, Long userId) {
        ProviderEntity provider = ProviderAssembler.toEntity(providerCreateRequest, userId);
        provider.setIsOfficial(false);
        llmDomainService.createProvider(provider);
        return ProviderAssembler.toDTO(provider);
    }

    /** 更新服务商
     * @param request 更新对象
     * @param userId 用户id
     * @return ProviderDTO */
    public ProviderDTO updateProvider(ProviderUpdateRequest request, Long userId) {
        // 先获取当前服务商数据
        ProviderEntity existingProvider = llmDomainService.getProvider(request.getId(), userId);

        // 判断是否需要保留原有的密钥
        if (request.getConfig() != null && request.getConfig().getApiKey() != null
                && request.getConfig().getApiKey().matches("\\*+")) {
            // 如果传入的是掩码，使用原有的密钥
            request.getConfig().setApiKey(existingProvider.getConfig().getApiKey());
        }

        ProviderEntity provider = ProviderAssembler.toEntity(request, userId);
        llmDomainService.updateProvider(provider);
        return ProviderAssembler.toDTO(provider);
    }

    /** 获取服务商
     * @param providerId 服务商id
     * @return ProviderDTO */
    public ProviderDTO getProvider(Long providerId, Long userId) {
        ProviderEntity provider = llmDomainService.getProvider(providerId, userId);
        return ProviderAssembler.toDTO(provider);
    }

    /** 删除服务商
     * @param providerId 服务商id
     * @param userId 用户id */
    public void deleteProvider(Long providerId, Long userId) {
        llmDomainService.deleteProvider(providerId, userId, Operator.USER);
    }

    /** 获取用户自己的服务商
     * @param userId 用户id
     * @return List<ProviderDTO> */
    public List<ProviderDTO> getUserProviders(Long userId) {
        List<ProviderAggregate> providers = llmDomainService.getUserProviders(userId);
        return providers.stream().map(ProviderAssembler::toDTO).collect(Collectors.toList());
    }

    /** 获取所有服务商（包含官方和用户自定义）
     * @param userId 用户ID
     * @return 服务商DTO列表 */
    public List<ProviderDTO> getAllProviders(Long userId) {
        List<ProviderAggregate> providers = llmDomainService.getAllProviders(userId);
        return providers.stream().map(ProviderAssembler::toDTO).collect(Collectors.toList());
    }

    /** 获取官方服务商
     * @return 官方服务商DTO列表 */
    public List<ProviderDTO> getOfficialProviders() {
        List<ProviderAggregate> providers = llmDomainService.getOfficialProviders();
        return providers.stream().map(ProviderAssembler::toDTO).collect(Collectors.toList());
    }

    /** 获取用户自定义服务商
     * @param userId 用户ID
     * @return 用户自定义服务商DTO列表 */
    public List<ProviderDTO> getCustomProviders(String userId) {
        List<ProviderAggregate> providers = llmDomainService.getCustomProviders(userId);
        return providers.stream().map(ProviderAssembler::toDTO).collect(Collectors.toList());
    }

    /** 获取用户服务商协议
     * @return List<ProviderProtocol> */
    public List<ProviderProtocol> getUserProviderProtocols() {
        return llmDomainService.getProviderProtocols();
    }

    /** 创建模型
     * @param modelCreateRequest 请求对象
     * @param userId 用户id
     * @return ModelDTO */
    public ModelDTO createModel(ModelCreateRequest modelCreateRequest, Long userId) {
        ModelEntity model = ModelAssembler.toEntity(modelCreateRequest, userId);
        // 用户创建默认是非官方
        model.setIsOfficial(false);
        llmDomainService.checkProviderExists(modelCreateRequest.getProviderId(), userId);
        llmDomainService.createModel(model);
        Long userDefaultModelId = userSettingsDomainService.getUserDefaultModelId(userId);
        // 如果用户没有默认模型则设置当前模型
        if (userDefaultModelId == null) {
            userSettingsDomainService.setUserDefaultModelId(userId, model.getId());
        }
        return ModelAssembler.toDTO(model);
    }

    /** 修改模型
     * @param request 请求对象
     * @param userId 用户id
     * @return ModelDTO */
    public ModelDTO updateModel(ModelUpdateRequest request, Long userId) {
        ModelEntity model = ModelAssembler.toEntity(request, userId);
        llmDomainService.updateModel(model);
        return ModelAssembler.toDTO(model);
    }

    /** 删除模型
     * @param modelId 模型id
     * @param userId 用户id */
    public void deleteModel(Long modelId, Long userId) {
        llmDomainService.deleteModel(modelId, userId, Operator.ADMIN);
    }

    /** 修改模型状态
     * @param modelId 模型id
     * @param userId 用户id */
    public void updateModelStatus(Long modelId, Long userId) {
        llmDomainService.updateModelStatus(modelId, userId);
    }

    /** 根据类型获取服务商
     * @param providerType 服务商类型编码：all-所有，official-官方，user-用户的
     * @param userId 用户ID
     * @return 服务商DTO列表 */
    public List<ProviderDTO> getProvidersByType(ProviderType providerType, Long userId) {
        // 使用枚举常量ProviderType代替硬编码字符串

        List<ProviderAggregate> providers = llmDomainService.getProvidersByType(providerType, userId);

        return providers.stream().map(ProviderAssembler::toDTO).collect(Collectors.toList());
    }

    /** 修改服务商状态
     * @param providerId 服务商id
     * @param userId 用户id */
    public void updateProviderStatus(String providerId, Long userId) {
        llmDomainService.updateProviderStatus(providerId, userId);
    }

    /** 获取所有激活模型
     * @param providerType 服务商类型
     * @param userId 用户id
     * @param modelType 模型类型（可选）
     * @return 模型列表 */
    public List<ModelDTO> getActiveModelsByType(ProviderType providerType, Long userId, ModelType modelType) {
        return llmDomainService.getProvidersByType(providerType, userId).stream().filter(ProviderAggregate::getStatus)
                .flatMap(provider -> provider.getModels().stream()
                        .filter(model -> modelType == null || model.getType() == modelType)
                        .map(model -> ModelAssembler.toDTO(model, provider.getName())))
                .collect(Collectors.toList());
    }

    public ModelDTO getDefaultModel(Long userId) {
        Long userDefaultModelId = userSettingsDomainService.getUserDefaultModelId(userId);
        ModelEntity modelEntity = llmDomainService.findModelById(userDefaultModelId);
        return ModelAssembler.toDTO(modelEntity);
    }

    /** 检查用户是否可以使用指定模型
     *
     * @param modelId 模型ID
     * @param userId 用户ID
     * @return 是否可以使用 */
    public boolean canUserUseModel(Long modelId, Long userId) {
        try {
            ModelEntity model = llmDomainService.getModelById(modelId);
            // 官方模型 + 激活状态 = 可用
            if (model.getIsOfficial() && model.getStatus()) {
                return true;
            }
            // 用户自己的模型 + 激活状态 = 可用
            if (userId.equals(model.getUserId()) && model.getStatus()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /** 获取用户可用的模型列表（用于外部API）
     *
     * @param userId 用户ID
     * @return 用户可用的聊天模型列表 */
    public List<ModelDTO> getAvailableModelsForUser(Long userId) {
        // 获取用户可用的模型：用户激活的 + 官方激活的聊天模型
        return getActiveModelsByType(ProviderType.ALL, userId, ModelType.CHAT);
    }
}
