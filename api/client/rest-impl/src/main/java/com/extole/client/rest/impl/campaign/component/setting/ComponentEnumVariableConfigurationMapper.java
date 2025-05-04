package com.extole.client.rest.impl.campaign.component.setting;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentEnumVariableConfiguration;
import com.extole.client.rest.campaign.configuration.VariableSource;
import com.extole.model.entity.campaign.EnumVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentEnumVariableConfigurationMapper
    implements ComponentSettingConfigurationMapper<CampaignComponentEnumVariableConfiguration> {

    @Override
    public CampaignComponentEnumVariableConfiguration mapToSettingConfiguration(Setting setting) {
        EnumVariable enumVariable = (EnumVariable) setting;
        return new CampaignComponentEnumVariableConfiguration(enumVariable.getName(),
            enumVariable.getDisplayName(),
            SettingType.valueOf(enumVariable.getType().name()),
            enumVariable.getValues(),
            VariableSource.valueOf(enumVariable.getSource().name()),
            enumVariable.getDescription(),
            enumVariable.getTags(),
            enumVariable.getPriority(),
            enumVariable.getAllowedValues().stream()
                .map(allowedValue -> allowedValue.getValue()).collect(
                    Collectors.toList()));

    }

    @Override
    public com.extole.model.entity.campaign.SettingType getSettingType() {
        return com.extole.model.entity.campaign.SettingType.ENUM;
    }

}
