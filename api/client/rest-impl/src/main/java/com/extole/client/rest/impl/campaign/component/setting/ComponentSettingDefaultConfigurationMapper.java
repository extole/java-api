package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentVariableConfiguration;
import com.extole.client.rest.campaign.configuration.VariableSource;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapperContext;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.Variable;

@Component
public class ComponentSettingDefaultConfigurationMapper
    implements ComponentSettingConfigurationMapper<CampaignComponentVariableConfiguration> {

    @Override
    public CampaignComponentVariableConfiguration mapToSettingConfiguration(
        CampaignComponentRestMapperContext restMapperContext, Setting setting) {
        Variable variable = (Variable) setting;
        return new CampaignComponentVariableConfiguration(variable.getName(),
            variable.getDisplayName(),
            SettingType.valueOf(variable.getType().name()),
            variable.getValues(),
            VariableSource.valueOf(variable.getSource().name()),
            variable.getDescription(),
            variable.getTags(),
            variable.getPriority());

    }

    @Override
    public List<com.extole.model.entity.campaign.SettingType> getSettingTypes() {
        return Collections.singletonList(com.extole.model.entity.campaign.SettingType.STRING);
    }

}
