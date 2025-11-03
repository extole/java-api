package com.extole.client.rest.impl.campaign.built.component.setting;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.component.setting.BuiltComponentEnumVariableResponse;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.id.Id;
import com.extole.model.entity.campaign.BuiltEnumVariable;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltSetting;

@Component
public class BuiltComponentEnumVariableRestMapper
    implements BuiltComponentSettingRestMapper<BuiltComponentEnumVariableResponse> {

    @Override
    public BuiltComponentEnumVariableResponse mapToSettingResponse(BuiltCampaign campaign, String componentId,
        BuiltSetting setting) {
        BuiltEnumVariable enumVariable = (BuiltEnumVariable) setting;
        return new BuiltComponentEnumVariableResponse(enumVariable.getName(),
            enumVariable.getDisplayName(),
            com.extole.client.rest.campaign.component.setting.SettingType.valueOf(enumVariable.getType().name()),
            enumVariable.getSourcedValues(),
            VariableSource.valueOf(enumVariable.getSource().name()),
            enumVariable.getDescription(),
            enumVariable.getTags(),
            Id.valueOf(enumVariable.getSourceComponentId().getValue()),
            enumVariable.getSourceVersion(),
            enumVariable.getPriority(),
            enumVariable.getAllowedValues().stream()
                .map(allowedValue -> allowedValue.getValue())
                .collect(Collectors.toList()));

    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.ENUM);
    }
}
