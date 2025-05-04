package com.extole.client.rest.impl.campaign.component.setting;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.service.campaign.setting.SettingBuilder;

@Component
public class SettingRequestMapperRepository {
    private final Map<SettingType, SettingCreateRequestMapper<? extends CampaignComponentSettingRequest,
        ? extends SettingBuilder>> settingCreateRequestHandlers;
    private final Map<SettingType, SettingUpdateRequestMapper<? extends CampaignComponentSettingUpdateRequest,
        ? extends SettingBuilder>> settingUpdateRequestHandlers;
    private final SettingDefaultCreateRequestMapper defaultCreateRequestHandler;
    private final SettingDefaultUpdateRequestMapper defaultUpdateRequestHandler;

    public SettingRequestMapperRepository(@Lazy List<SettingCreateRequestMapper<?, ?>> settingCreateRequestMappers,
        @Lazy List<SettingUpdateRequestMapper<?, ?>> settingUpdateRequestMappers,
        SettingDefaultCreateRequestMapper defaultCreateRequestHandler,
        SettingDefaultUpdateRequestMapper defaultUpdateRequestHandler) {
        this.settingCreateRequestHandlers = settingCreateRequestMappers.stream()
            .collect(Collectors.toMap(handler -> handler.getSettingType(), handler -> handler));
        this.settingUpdateRequestHandlers = settingUpdateRequestMappers.stream()
            .collect(Collectors.toMap(handler -> handler.getSettingType(), handler -> handler));
        this.defaultCreateRequestHandler = defaultCreateRequestHandler;
        this.defaultUpdateRequestHandler = defaultUpdateRequestHandler;
    }

    public SettingCreateRequestMapper getCreateRequestMapper(SettingType settingType) {
        if (settingCreateRequestHandlers.containsKey(settingType)) {
            return settingCreateRequestHandlers.get(settingType);
        }

        return defaultCreateRequestHandler;
    }

    public SettingUpdateRequestMapper getUpdateRequestMapper(SettingType settingType) {
        if (settingUpdateRequestHandlers.containsKey(settingType)) {
            return settingUpdateRequestHandlers.get(settingType);
        }

        return defaultUpdateRequestHandler;
    }
}
