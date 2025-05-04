package com.extole.client.rest.impl.campaign.component.setting;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.VariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class SettingDefaultUpdateRequestMapper
    implements SettingUpdateRequestMapper<CampaignComponentVariableUpdateRequest, VariableBuilder> {

    @Override
    public void complete(CampaignComponentVariableUpdateRequest updateRequest, VariableBuilder builder)
        throws VariableValueKeyLengthException {
        updateRequest.getValues().ifPresent(values -> builder.withValues(values));
        updateRequest.getSource().ifPresent(source -> builder
            .withSource(VariableSource.valueOf(source.name())));
        updateRequest.getDescription().ifPresent(description -> builder.withDescription(description));
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.STRING;
    }
}
