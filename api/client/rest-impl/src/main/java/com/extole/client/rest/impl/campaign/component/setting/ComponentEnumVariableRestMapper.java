package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentEnumVariableResponse;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.model.entity.campaign.EnumVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentEnumVariableRestMapper
    implements ComponentSettingRestMapper<CampaignComponentEnumVariableResponse> {

    @Override
    public CampaignComponentEnumVariableResponse mapToSettingResponse(Setting setting) {
        EnumVariable enumVariable = (EnumVariable) setting;
        return new CampaignComponentEnumVariableResponse(enumVariable.getName(),
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
    public List<com.extole.model.entity.campaign.SettingType> getSettingTypes() {
        return Collections.singletonList(com.extole.model.entity.campaign.SettingType.ENUM);
    }
}
