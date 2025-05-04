package com.extole.client.rest.impl.campaign.component.setting;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentClientKeyFlowVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.id.Id;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.ClientKeyFlowVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class ClientKeyFlowVariableCreateRequestMapper
    implements
    SettingCreateRequestMapper<CampaignComponentClientKeyFlowVariableRequest, ClientKeyFlowVariableBuilder> {

    @Override
    public void complete(CampaignComponentClientKeyFlowVariableRequest createRequest,
        ClientKeyFlowVariableBuilder builder) throws VariableValueKeyLengthException {
        createRequest.getValues().ifPresent(values -> builder.withValues(values));
        createRequest.getSource().ifPresent(source -> builder.withSource(VariableSource.valueOf(source.name())));
        createRequest.getDescription().ifPresent(description -> builder.withDescription(description));
        createRequest.getRedirectUri().ifPresent(value -> builder.withRedirectUri(value));
        createRequest.getClientKeyUrl().ifPresent(value -> builder.withClientKeyUrl(value));
        createRequest.getClientKeyOAuthFlow()
            .ifPresent(value -> builder.withClientKeyOAuthFlow(Id.valueOf(value.toString())));
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.CLIENT_KEY_FLOW;
    }

}
