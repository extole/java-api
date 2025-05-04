package com.extole.client.rest.impl.campaign.component.setting;

import static java.util.stream.Collectors.toList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketFilterConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentVariableConfiguration;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.Socket;

@Component
public class ComponentSocketConfigurationMapper
    implements ComponentSettingConfigurationMapper<CampaignComponentSocketConfiguration> {

    private final CampaignComponentSettingRestMapper componentSettingRestMapper;

    @Autowired
    public ComponentSocketConfigurationMapper(@Lazy CampaignComponentSettingRestMapper componentSettingRestMapper) {
        this.componentSettingRestMapper = componentSettingRestMapper;
    }

    @Override
    public CampaignComponentSocketConfiguration mapToSettingConfiguration(Setting setting) {
        Socket socket = (Socket) setting;
        return new CampaignComponentSocketConfiguration(socket.getName(),
            socket.getDisplayName(),
            SettingType.valueOf(socket.getType().name()),
            new CampaignComponentSocketFilterConfiguration(socket.getFilter().getComponentType()),
            socket.getDescription(),
            socket.getTags(),
            socket.getPriority(),
            socket.getParameters().stream()
                .map(parameter -> componentSettingRestMapper.toSettingConfiguration(parameter))
                .map(CampaignComponentVariableConfiguration.class::cast)
                .collect(toList()));

    }

    @Override
    public com.extole.model.entity.campaign.SettingType getSettingType() {
        return com.extole.model.entity.campaign.SettingType.MULTI_SOCKET;
    }

}
