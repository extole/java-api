package com.extole.client.rest.impl.campaign.component.setting;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentVariableConfiguration;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapperContext;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.Socket;

@Component
public class ComponentSocketConfigurationMapper
    implements ComponentSettingConfigurationMapper<CampaignComponentSocketConfiguration> {

    private final CampaignComponentSettingRestMapper componentSettingRestMapper;
    private final ComponentSocketFilterMapper componentSocketFilterMapper;

    @Autowired
    public ComponentSocketConfigurationMapper(@Lazy CampaignComponentSettingRestMapper componentSettingRestMapper,
        ComponentSocketFilterMapper componentSocketFilterMapper) {
        this.componentSettingRestMapper = componentSettingRestMapper;
        this.componentSocketFilterMapper = componentSocketFilterMapper;
    }

    @Override
    public CampaignComponentSocketConfiguration mapToSettingConfiguration(
        CampaignComponentRestMapperContext restMapperContext, Setting setting) {
        Socket socket = (Socket) setting;
        return new CampaignComponentSocketConfiguration(socket.getName(),
            socket.getDisplayName(),
            SettingType.valueOf(socket.getType().name()),
            socket.getFilters().stream().map(componentSocketFilterMapper::mapToConfiguration).toList(),
            socket.getDescription(),
            socket.getTags(),
            socket.getPriority(),
            socket.getParameters().stream()
                .map(parameter -> componentSettingRestMapper.toSettingConfiguration(restMapperContext, parameter))
                .map(CampaignComponentVariableConfiguration.class::cast)
                .collect(toList()));

    }

    @Override
    public List<com.extole.model.entity.campaign.SettingType> getSettingTypes() {
        return List.of(
            com.extole.model.entity.campaign.SettingType.MULTI_SOCKET,
            com.extole.model.entity.campaign.SettingType.SOCKET);
    }

}
