package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentEnumListVariableConfiguration;
import com.extole.client.rest.campaign.configuration.VariableSource;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapperContext;
import com.extole.model.entity.campaign.EnumListVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentEnumListVariableConfigurationMapper
    implements ComponentSettingConfigurationMapper<CampaignComponentEnumListVariableConfiguration> {

    @Override
    public CampaignComponentEnumListVariableConfiguration mapToSettingConfiguration(
        CampaignComponentRestMapperContext restMapperContext, Setting setting) {
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
                .map(allowedValue -> allowedValue.getValue()).toList());

    }

    @Override
    public List<com.extole.model.entity.campaign.SettingType> getSettingTypes() {
        return Collections.singletonList(com.extole.model.entity.campaign.SettingType.ENUM_LIST);
    }
}
