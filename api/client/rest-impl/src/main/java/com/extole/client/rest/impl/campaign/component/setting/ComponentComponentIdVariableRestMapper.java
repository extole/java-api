package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentComponentIdVariableResponse;
import com.extole.client.rest.campaign.component.setting.ComponentIdFilterResponse;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.model.entity.campaign.ComponentIdVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentComponentIdVariableRestMapper
    implements ComponentSettingRestMapper<CampaignComponentComponentIdVariableResponse> {

    @Override
    public CampaignComponentComponentIdVariableResponse mapToSettingResponse(Setting setting) {
        ComponentIdVariable variable = (ComponentIdVariable) setting;
        return new CampaignComponentComponentIdVariableResponse(variable.getName(),
            variable.getDisplayName(),
            SettingType.valueOf(variable.getType().name()),
            variable.getValues(),
            VariableSource.valueOf(variable.getSource().name()),
            variable.getDescription(),
            variable.getTags(),
            variable.getPriority(),
            new ComponentIdFilterResponse(variable.getFilter().getComponentTypes()));
    }

    @Override
    public List<com.extole.model.entity.campaign.SettingType> getSettingTypes() {
        return Collections.singletonList(com.extole.model.entity.campaign.SettingType.COMPONENT_ID);
    }
}
