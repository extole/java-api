package com.extole.client.rest.impl.campaign.component.setting;

import static java.util.stream.Collectors.toList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentSocketResponse;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableResponse;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.SocketFilterResponse;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.Socket;

@Component
public class ComponentSocketRestMapper implements ComponentSettingRestMapper<CampaignComponentSocketResponse> {

    private final CampaignComponentSettingRestMapper componentSettingRestMapper;

    @Autowired
    public ComponentSocketRestMapper(@Lazy CampaignComponentSettingRestMapper componentSettingRestMapper) {
        this.componentSettingRestMapper = componentSettingRestMapper;
    }

    @Override
    public CampaignComponentSocketResponse mapToSettingResponse(Setting setting) {
        Socket socket = (Socket) setting;
        return new CampaignComponentSocketResponse(socket.getName(),
            socket.getDisplayName(),
            SettingType.valueOf(socket.getType().name()),
            socket.getDescription(),
            new SocketFilterResponse(socket.getFilter().getComponentType()),
            socket.getTags(),
            socket.getPriority(),
            socket.getParameters().stream()
                .map(parameter -> componentSettingRestMapper.toSettingResponse(parameter))
                .map(CampaignComponentVariableResponse.class::cast)
                .collect(toList()));

    }

    @Override
    public com.extole.model.entity.campaign.SettingType getSettingType() {
        return com.extole.model.entity.campaign.SettingType.MULTI_SOCKET;
    }

}
