package com.extole.client.rest.impl.campaign.component.setting;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentDelayListVariableConfiguration;
import com.extole.client.rest.campaign.configuration.VariableSource;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.Variable;

@Component
public class ComponentDelayListVariableConfigurationMapper
    implements ComponentSettingConfigurationMapper<CampaignComponentDelayListVariableConfiguration> {

    @Override
    public CampaignComponentDelayListVariableConfiguration mapToSettingConfiguration(Setting setting) {
        Variable enumListVariable = (Variable) setting;
        return new CampaignComponentDelayListVariableConfiguration(enumListVariable.getName(),
            enumListVariable.getDisplayName(),
            SettingType.valueOf(enumListVariable.getType().name()),
            enumListVariable.getValues(),
            VariableSource.valueOf(enumListVariable.getSource().name()),
            enumListVariable.getDescription(),
            enumListVariable.getTags(),
            enumListVariable.getPriority());

    }

    @Override
    public com.extole.model.entity.campaign.SettingType getSettingType() {
        return com.extole.model.entity.campaign.SettingType.DELAY_LIST;
    }
}
