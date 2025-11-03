package com.extole.client.rest.impl.campaign.component.setting;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
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

    public SettingRequestMapperRepository(
        @Lazy List<SettingCreateRequestMapper<?, ?>> settingCreateRequestMappers,
        @Lazy List<SettingUpdateRequestMapper<?, ?>> settingUpdateRequestMappers,
        SettingDefaultCreateRequestMapper defaultCreateRequestHandler,
        SettingDefaultUpdateRequestMapper defaultUpdateRequestHandler) {
        this.settingCreateRequestHandlers = createRequestsAsMap(settingCreateRequestMappers);
        this.settingUpdateRequestHandlers = updateRequestsAsMap(settingUpdateRequestMappers);
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

    private
        Map<SettingType,
            SettingCreateRequestMapper<? extends CampaignComponentSettingRequest, ? extends SettingBuilder>>
        createRequestsAsMap(
            List<SettingCreateRequestMapper<?, ?>> settingCreateRequestMappers) {
        ImmutableMap.Builder<SettingType,
            SettingCreateRequestMapper<? extends CampaignComponentSettingRequest, ? extends SettingBuilder>> builder =
                ImmutableMap.builder();
        settingCreateRequestMappers.forEach(item -> item.getSettingTypes().forEach(
            settingType -> builder.put(settingType, item)));
        return builder.build();
    }

    private
        Map<SettingType,
            SettingUpdateRequestMapper<? extends CampaignComponentSettingUpdateRequest, ? extends SettingBuilder>>
        updateRequestsAsMap(
            List<SettingUpdateRequestMapper<?, ?>> settingUpdateRequestMappers) {
        ImmutableMap.Builder<SettingType,
            SettingUpdateRequestMapper<? extends CampaignComponentSettingUpdateRequest,
                ? extends SettingBuilder>> builder =
                    ImmutableMap.builder();
        settingUpdateRequestMappers.forEach(item -> item.getSettingTypes().forEach(
            settingType -> builder.put(settingType, item)));
        return builder.build();
    }
}
