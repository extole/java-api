package com.extole.client.rest.impl.campaign.built.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.component.setting.BuiltComponentClientKeyFlowVariableResponse;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.id.Id;
import com.extole.model.entity.campaign.BuiltClientKeyFlowVariable;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltSetting;

@Component
public class BuiltComponentClientKeyFlowVariableRestMapper
    implements BuiltComponentSettingRestMapper<BuiltComponentClientKeyFlowVariableResponse> {
    @Override
    public BuiltComponentClientKeyFlowVariableResponse mapToSettingResponse(BuiltCampaign campaign, String componentId,
        BuiltSetting setting) {
        BuiltClientKeyFlowVariable clientKeyFlowVariable = (BuiltClientKeyFlowVariable) setting;
        return new BuiltComponentClientKeyFlowVariableResponse(setting.getName(),
            setting.getDisplayName(),
            com.extole.client.rest.campaign.component.setting.SettingType.valueOf(setting.getType().name()),
            clientKeyFlowVariable.getSourcedValues(),
            VariableSource.valueOf(clientKeyFlowVariable.getSource().name()),
            clientKeyFlowVariable.getDescription(),
            setting.getTags(),
            Id.valueOf(clientKeyFlowVariable.getSourceComponentId().getValue()),
            clientKeyFlowVariable.getSourceVersion(),
            clientKeyFlowVariable.getPriority(),
            clientKeyFlowVariable.getRedirectUri(),
            clientKeyFlowVariable.getClientKeyUrl(),
            clientKeyFlowVariable.getClientKeyOauthFlow().getValue());

    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.CLIENT_KEY_FLOW);
    }
}
