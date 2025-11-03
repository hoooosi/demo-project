package io.github.hoooosi.agentplus.application.llm.assembler;

import io.github.hoooosi.agentplus.application.llm.dto.ModelDTO;
import io.github.hoooosi.agentplus.domain.llm.model.ModelEntity;
import io.github.hoooosi.agentplus.interfaces.dto.llm.request.ModelCreateRequest;
import io.github.hoooosi.agentplus.interfaces.dto.llm.request.ModelUpdateRequest;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** 模型对象转换器 */
public class ModelAssembler {

    /** 将领域对象转换为DTO */
    public static ModelDTO toDTO(ModelEntity model) {
        if (model == null) {
            return null;
        }

        return new ModelDTO()
                .setId(model.getId())
                .setUserId(model.getUserId())
                .setProviderId(model.getProviderId())
                .setModelId(model.getModelId())
                .setName(model.getName())
                .setDescription(model.getDescription())
                .setType(model.getType())
                .setStatus(model.getStatus())
                .setModelEndpoint(model.getModelEndpoint())
                .setCreatedAt(model.getCreatedAt())
                .setUpdatedAt(model.getUpdatedAt())
                .setIsOfficial(model.getIsOfficial());
    }

    /** 将领域对象转换为DTO，并设置服务商名称 */
    public static ModelDTO toDTO(ModelEntity model, String providerName) {
        ModelDTO dto = toDTO(model);
        if (dto != null) {
            dto.setProviderName(providerName);
        }
        return dto;
    }

    /** 将多个领域对象转换为DTO列表 */
    public static List<ModelDTO> toDTOs(List<ModelEntity> models) {
        if (models == null || models.isEmpty()) {
            return Collections.emptyList();
        }
        return models.stream().map(ModelAssembler::toDTO).collect(Collectors.toList());
    }

    /** 将创建请求转换为领域对象 */
    public static ModelEntity toEntity(ModelCreateRequest request, Long userId) {
        ModelEntity model = new ModelEntity()
                .setUserId(userId)
                .setProviderId(request.getProviderId())
                .setModelId(request.getModelId())
                .setName(request.getName())
                .setDescription(request.getDescription())
                .setType(request.getType())
                .setModelEndpoint(request.getModelEndpoint());

        if (ObjectUtils.isEmpty(request.getModelEndpoint())) {
            model.setModelEndpoint(request.getModelId());
        }

        return model;
    }

    public static ModelEntity toEntity(ModelUpdateRequest request, Long userId) {
        ModelEntity model = new ModelEntity()
                .setUserId(userId)
                .setName(request.getName())
                .setDescription(request.getDescription())
                .setModelId(request.getModelId())
                .setModelEndpoint(request.getModelEndpoint())
                .setId(request.getId())
                .setModelEndpoint(request.getModelEndpoint());
        if (ObjectUtils.isEmpty(request.getModelEndpoint())) {
            model.setModelEndpoint(request.getModelId());
        }

        return model;
    }
}
