package io.github.hoooosi.agentplus.application.llm.assembler;

import io.github.hoooosi.agentplus.application.llm.dto.ModelDTO;
import io.github.hoooosi.agentplus.application.llm.dto.ProviderDTO;
import io.github.hoooosi.agentplus.domain.llm.model.ModelEntity;
import io.github.hoooosi.agentplus.domain.llm.model.ProviderAggregate;
import io.github.hoooosi.agentplus.domain.user.service.model.ProviderEntity;
import io.github.hoooosi.agentplus.interfaces.dto.users.llm.request.ProviderCreateRequest;
import io.github.hoooosi.agentplus.interfaces.dto.users.llm.request.ProviderUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/** 服务提供商对象转换器 */
public class ProviderAssembler {
    /** 将实体转换为DTO，并进行敏感信息脱敏 */
    public static ProviderDTO toDTO(ProviderEntity provider) {
        if (provider == null) {
            return null;
        }

        ProviderDTO dto = new ProviderDTO()
                .setId(provider.getId())
                .setProtocol(provider.getProtocol())
                .setName(provider.getName())
                .setDescription(provider.getDescription())
                .setConfig(provider.getConfig())
                .setIsOfficial(provider.getIsOfficial())
                .setStatus(provider.getStatus())
                .setCreatedAt(provider.getCreatedAt())
                .setUpdatedAt(provider.getUpdatedAt());

        // 脱敏处理（针对返回前端的场景）
        dto.maskSensitiveInfo();

        return dto;
    }

    /** 将多个聚合根转换为DTO列表 */
    public static List<ProviderDTO> toDTOList(List<ProviderAggregate> providers) {
        return providers.stream().map(ProviderAssembler::toDTO).collect(Collectors.toList());
    }

    /** 将多个实体转换为DTO列表 */
    public static List<ProviderDTO> toDTOListFromEntities(List<ProviderEntity> providers) {
        return providers.stream().map(ProviderAssembler::toDTO).collect(Collectors.toList());
    }

    /** 将创建请求转换为实体 */
    public static ProviderEntity toEntity(ProviderCreateRequest request, Long userId) {
        ProviderEntity provider = new ProviderEntity();
        provider.setUserId(userId);
        provider.setProtocol(request.getProtocol());
        provider.setName(request.getName());
        provider.setDescription(request.getDescription());
        provider.setConfig(request.getConfig()); // 会自动加密
        provider.setStatus(request.getStatus());
        provider.setCreatedAt(System.currentTimeMillis());
        provider.setUpdatedAt(System.currentTimeMillis());
        return provider;
    }

    /** 将更新请求转换为实体 */
    public static ProviderEntity toEntity(ProviderUpdateRequest request, Long userId) {
        ProviderEntity provider = new ProviderEntity();
        provider.setId(request.getId());
        provider.setUserId(userId);
        provider.setProtocol(request.getProtocol());
        provider.setName(request.getName());
        provider.setDescription(request.getDescription());
        provider.setConfig(request.getConfig()); // 会自动加密
        provider.setStatus(request.getStatus());
        provider.setUpdatedAt(System.currentTimeMillis());
        return provider;
    }

    /** 根据更新请求更新实体 */
    public static void updateEntity(ProviderEntity entity, ProviderUpdateRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getName() != null) {
            entity.setName(request.getName());
        }

        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }

        if (request.getConfig() != null) {
            entity.setConfig(request.getConfig()); // 会自动加密
        }

        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }

        entity.setUpdatedAt(System.currentTimeMillis());
    }

    // 将聚合根转换为dto
    public static ProviderDTO toDTO(ProviderAggregate provider) {
        if (provider == null) {
            return null;
        }
        ProviderDTO dto = toDTO(provider.getEntity());

        List<ModelEntity> models = provider.getModels();
        if (models == null || models.isEmpty()) {
            return dto;
        }
        for (ModelEntity model : models) {
            ModelDTO modelDTO = ModelAssembler.toDTO(model, provider.getName());
            modelDTO.setIsOfficial(provider.getEntity().getIsOfficial());
            dto.getModels().add(modelDTO);
        }
        return dto;
    }
}
