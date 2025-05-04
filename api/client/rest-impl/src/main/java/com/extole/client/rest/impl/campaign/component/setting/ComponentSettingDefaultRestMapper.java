package com.extole.client.rest.impl.campaign.component.setting;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableResponse;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.Variable;

@Component
public class ComponentSettingDefaultRestMapper
    implements ComponentSettingRestMapper<CampaignComponentVariableResponse> {

    @Override
    public CampaignComponentVariableResponse mapToSettingResponse(Setting setting) {
        Variable variable = (Variable) setting;
        return new CampaignComponentVariableResponse(variable.getName(),
            variable.getDisplayName(),
            SettingType.valueOf(variable.getType().name()),
            variable.getValues(),
            VariableSource.valueOf(variable.getSource().name()),
            variable.getDescription(),
            variable.getTags(),
            variable.getPriority());

    }

    @Override
    public com.extole.model.entity.campaign.SettingType getSettingType() {
        return com.extole.model.entity.campaign.SettingType.STRING;
    }
}
