package com.extole.client.rest.impl.campaign.component.setting;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentEnumListVariableConfiguration;
import com.extole.client.rest.campaign.configuration.VariableSource;
import com.extole.model.entity.campaign.EnumListVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentEnumListVariableConfigurationMapper
    implements ComponentSettingConfigurationMapper<CampaignComponentEnumListVariableConfiguration> {

    @Override
    public CampaignComponentEnumListVariableConfiguration mapToSettingConfiguration(Setting setting) {
        EnumListVariable enumListVariable = (EnumListVariable) setting;
        return new CampaignComponentEnumListVariableConfiguration(enumListVariable.getName(),
            enumListVariable.getDisplayName(),
            SettingType.valueOf(enumListVariable.getType().name()),
            enumListVariable.getValues(),
            VariableSource.valueOf(enumListVariable.getSource().name()),
            enumListVariable.getDescription(),
            enumListVariable.getTags(),
            enumListVariable.getPriority(),
            enumListVariable.getAllowedValues().stream()
                .map(allowedValue -> allowedValue.getValue()).collect(
                    Collectors.toUnmodifiableList()));

    }

    @Override
    public com.extole.model.entity.campaign.SettingType getSettingType() {
        return com.extole.model.entity.campaign.SettingType.ENUM_LIST;
    }
}
