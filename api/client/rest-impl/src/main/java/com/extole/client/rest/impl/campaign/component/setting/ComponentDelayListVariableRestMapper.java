package com.extole.client.rest.impl.campaign.component.setting;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentDelayListVariableResponse;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.model.entity.campaign.DelayListVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentDelayListVariableRestMapper
    implements ComponentSettingRestMapper<CampaignComponentDelayListVariableResponse> {

    @Override
    public CampaignComponentDelayListVariableResponse mapToSettingResponse(Setting setting) {
        DelayListVariable delayListVariable = (DelayListVariable) setting;
        return new CampaignComponentDelayListVariableResponse(delayListVariable.getName(),
            delayListVariable.getDisplayName(),
            SettingType.valueOf(delayListVariable.getType().name()),
            delayListVariable.getValues(),
            VariableSource.valueOf(delayListVariable.getSource().name()),
            delayListVariable.getDescription(),
            delayListVariable.getTags(),
            delayListVariable.getPriority());

    }

    @Override
    public com.extole.model.entity.campaign.SettingType getSettingType() {
        return com.extole.model.entity.campaign.SettingType.DELAY_LIST;
    }
}
