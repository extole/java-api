package com.extole.client.rest.impl.campaign.component.setting;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentClientKeyFlowVariableConfiguration;
import com.extole.client.rest.campaign.configuration.VariableSource;
import com.extole.model.entity.campaign.ClientKeyFlowVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentClientKeyFlowVariableConfigurationMapper
    implements ComponentSettingConfigurationMapper<CampaignComponentClientKeyFlowVariableConfiguration> {
    @Override
    public CampaignComponentClientKeyFlowVariableConfiguration mapToSettingConfiguration(Setting setting) {
        ClientKeyFlowVariable clientKeyFlowVariable = (ClientKeyFlowVariable) setting;
        return new CampaignComponentClientKeyFlowVariableConfiguration(setting.getName(),
            setting.getDisplayName(),
            SettingType.valueOf(setting.getType().name()),
            clientKeyFlowVariable.getValues(),
            VariableSource.valueOf(clientKeyFlowVariable.getSource().name()),
            clientKeyFlowVariable.getDescription(),
            setting.getTags(),
            setting.getPriority(),
            clientKeyFlowVariable.getRedirectUri(),
            clientKeyFlowVariable.getClientKeyUrl(),
            clientKeyFlowVariable.getClientKeyOauthFlow().getValue());

    }

    @Override
    public com.extole.model.entity.campaign.SettingType getSettingType() {
        return com.extole.model.entity.campaign.SettingType.CLIENT_KEY_FLOW;
    }

}
