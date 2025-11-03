package com.extole.client.rest.impl.campaign.built.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.component.setting.BuiltComponentComponentIdVariableResponse;
import com.extole.client.rest.campaign.component.setting.ComponentIdFilterResponse;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.id.Id;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltComponentIdVariable;
import com.extole.model.entity.campaign.built.BuiltSetting;

@Component
public class BuiltComponentComponentIdVariableRestMapper
    implements BuiltComponentSettingRestMapper<BuiltComponentComponentIdVariableResponse> {

    @Override
    public BuiltComponentComponentIdVariableResponse mapToSettingResponse(BuiltCampaign campaign,
        String componentId, BuiltSetting setting) {
        BuiltComponentIdVariable variable = (BuiltComponentIdVariable) setting;
        return new BuiltComponentComponentIdVariableResponse(variable.getName(),
            variable.getDisplayName(),
            com.extole.client.rest.campaign.component.setting.SettingType.valueOf(variable.getType().name()),
            variable.getSourcedValues(),
            VariableSource.valueOf(variable.getSource().name()),
            variable.getDescription(),
            variable.getTags(),
            Id.valueOf(variable.getSourceComponentId().getValue()),
            variable.getSourceVersion(),
            variable.getPriority(),
            new ComponentIdFilterResponse(variable.getFilter().componentTypes()));
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.COMPONENT_ID);
    }
}
