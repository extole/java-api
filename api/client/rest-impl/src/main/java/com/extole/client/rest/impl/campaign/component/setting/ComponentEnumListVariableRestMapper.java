package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentEnumListVariableResponse;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.model.entity.campaign.EnumListVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentEnumListVariableRestMapper
    implements ComponentSettingRestMapper<CampaignComponentEnumListVariableResponse> {

    @Override
    public CampaignComponentEnumListVariableResponse mapToSettingResponse(Setting setting) {
        EnumListVariable enumListVariable = (EnumListVariable) setting;
        return new CampaignComponentEnumListVariableResponse(enumListVariable.getName(),
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
