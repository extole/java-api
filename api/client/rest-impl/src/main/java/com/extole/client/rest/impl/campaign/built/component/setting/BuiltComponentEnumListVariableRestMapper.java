package com.extole.client.rest.impl.campaign.built.component.setting;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.component.setting.BuiltComponentEnumListVariableResponse;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.id.Id;
import com.extole.model.entity.campaign.BuiltEnumListVariable;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltSetting;

@Component
public class BuiltComponentEnumListVariableRestMapper
    implements BuiltComponentSettingRestMapper<BuiltComponentEnumListVariableResponse> {

    @Override
    public BuiltComponentEnumListVariableResponse mapToSettingResponse(BuiltCampaign campaign, String componentId,
        BuiltSetting setting) {
        BuiltEnumListVariable enumVariable = (BuiltEnumListVariable) setting;
        return new BuiltComponentEnumListVariableResponse(enumVariable.getName(),
            enumVariable.getDisplayName(),
            com.extole.client.rest.campaign.component.setting.SettingType.valueOf(enumVariable.getType().name()),
            enumVariable.getSourcedValues(),
            VariableSource.valueOf(enumVariable.getSource().name()),
            enumVariable.getDescription(),
            enumVariable.getTags(),
            Id.valueOf(enumVariable.getSourceComponentId().getValue()),
            enumVariable.getPriority(),
            enumVariable.getAllowedValues().stream()
                .map(allowedValue -> allowedValue.getValue())
                .collect(Collectors.toList()));

    }

    @Override
    public SettingType getSettingType() {
        return SettingType.ENUM_LIST;
    }
}
