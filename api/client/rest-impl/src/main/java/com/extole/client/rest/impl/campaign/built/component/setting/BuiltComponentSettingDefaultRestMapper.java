package com.extole.client.rest.impl.campaign.built.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.component.setting.BuiltCampaignComponentVariableResponse;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.id.Id;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltSetting;
import com.extole.model.entity.campaign.built.BuiltVariable;

@Component
public class BuiltComponentSettingDefaultRestMapper
    implements BuiltComponentSettingRestMapper<BuiltCampaignComponentVariableResponse> {

    @Override
    public BuiltCampaignComponentVariableResponse mapToSettingResponse(BuiltCampaign campaign, String componentId,
        BuiltSetting setting) {
        BuiltVariable variable = (BuiltVariable) setting;
        return new BuiltCampaignComponentVariableResponse(variable.getName(),
            variable.getDisplayName(),
            com.extole.client.rest.campaign.component.setting.SettingType.valueOf(variable.getType().name()),
            variable.getSourcedValues(),
            VariableSource.valueOf(variable.getSource().name()),
            variable.getDescription(),
            variable.getTags(),
            Id.valueOf(variable.getSourceComponentId().getValue()),
            variable.getSourceVersion(),
            variable.getPriority());

    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.STRING);
    }
}
