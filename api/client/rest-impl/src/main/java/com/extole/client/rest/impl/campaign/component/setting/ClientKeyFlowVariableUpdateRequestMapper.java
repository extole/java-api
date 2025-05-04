package com.extole.client.rest.impl.campaign.component.setting;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentClientKeyFlowVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.id.Id;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.ClientKeyFlowVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class ClientKeyFlowVariableUpdateRequestMapper
    implements
    SettingUpdateRequestMapper<CampaignComponentClientKeyFlowVariableUpdateRequest, ClientKeyFlowVariableBuilder> {

    @Override
    public void complete(
        CampaignComponentClientKeyFlowVariableUpdateRequest updateRequest, ClientKeyFlowVariableBuilder builder)
        throws VariableValueKeyLengthException {
        updateRequest.getValues().ifPresent(values -> builder.withValues(values));
        updateRequest.getSource().ifPresent(source -> builder
            .withSource(VariableSource.valueOf(source.name())));
        updateRequest.getDescription().ifPresent(description -> builder.withDescription(description));
        updateRequest.getRedirectUri().ifPresent(builder::withRedirectUri);
        updateRequest.getClientKeyUrl().ifPresent(builder::withClientKeyUrl);
        updateRequest.getClientKeyOauthFlow()
            .ifPresent(clientKeyOauthFlow -> builder.withClientKeyOAuthFlow(Id.valueOf(clientKeyOauthFlow.toString())));
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.CLIENT_KEY_FLOW;
    }
}
