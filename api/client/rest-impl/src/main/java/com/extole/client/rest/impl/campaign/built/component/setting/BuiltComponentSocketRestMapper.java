package com.extole.client.rest.impl.campaign.built.component.setting;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.component.setting.BuiltCampaignComponentVariableResponse;
import com.extole.client.rest.campaign.built.component.setting.BuiltComponentSocketResponse;
import com.extole.client.rest.impl.campaign.component.setting.CampaignComponentSettingRestMapper;
import com.extole.client.rest.impl.campaign.component.setting.ComponentSocketFilterMapper;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltSetting;
import com.extole.model.entity.campaign.built.BuiltSocket;

@Component
public class BuiltComponentSocketRestMapper implements BuiltComponentSettingRestMapper<BuiltComponentSocketResponse> {

    private final CampaignComponentSettingRestMapper componentSettingRestMapper;
    private final ComponentSocketFilterMapper componentSocketFilterMapper;

    @Autowired
    public BuiltComponentSocketRestMapper(@Lazy CampaignComponentSettingRestMapper componentSettingRestMapper,
        ComponentSocketFilterMapper componentSocketFilterMapper) {
        this.componentSettingRestMapper = componentSettingRestMapper;
        this.componentSocketFilterMapper = componentSocketFilterMapper;
    }

    @Override
    public BuiltComponentSocketResponse mapToSettingResponse(BuiltCampaign campaign, String componentId,
        BuiltSetting setting) {
        BuiltSocket socket = (BuiltSocket) setting;

        List<String> installedComponentIds = campaign.getComponents()
            .stream()
            .filter(component -> component.getInstalledIntoSocket().isPresent()
                && component.getInstalledIntoSocket().get().equals(setting.getName()))
            .filter(component -> component.getComponentReferences().stream()
                .anyMatch(reference -> reference.getComponentId().equals(Id.valueOf(componentId))))
            .map(component -> component.getId().getValue())
            .collect(Collectors.toList());

        try {
            return new BuiltComponentSocketResponse(socket.getName(),
                socket.getDisplayName(),
                com.extole.client.rest.campaign.component.setting.SettingType.valueOf(socket.getType().name()),
                Map.of("default",
                    ObjectMapperProvider.getConfiguredInstance().readValue(
                        ObjectMapperProvider.getConfiguredInstance().writeValueAsString(installedComponentIds),
                        new TypeReference<>() {})),
                componentSocketFilterMapper.mapToSocketFilter(socket.getFilter()),
                socket.getFilters().stream().map(componentSocketFilterMapper::mapToSocketFilter).toList(),
                socket.getDescription(),
                socket.getTags(),
                socket.getPriority(),
                socket.getParameters().stream().map(parameter -> componentSettingRestMapper
                    .toBuiltSettingResponse(campaign, componentId, parameter))
                    .map(BuiltCampaignComponentVariableResponse.class::cast)
                    .collect(toList()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Should not happen", e);
        }
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return List.of(SettingType.MULTI_SOCKET, SettingType.SOCKET);
    }

}
