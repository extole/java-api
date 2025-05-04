package com.extole.client.rest.impl.campaign.component.setting;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentEnumVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.EnumVariableMember;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.EnumVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class EnumVariableUpdateRequestMapper
    implements SettingUpdateRequestMapper<CampaignComponentEnumVariableUpdateRequest, EnumVariableBuilder> {

    @Override
    public void complete(
        CampaignComponentEnumVariableUpdateRequest updateRequest, EnumVariableBuilder builder)
        throws VariableValueKeyLengthException {
        updateRequest.getValues().ifPresent(values -> builder.withValues(values));
        updateRequest.getSource().ifPresent(source -> builder
            .withSource(VariableSource.valueOf(source.name())));
        updateRequest.getDescription().ifPresent(description -> builder.withDescription(description));
        updateRequest.getAllowedValues().ifPresent(
            allowedValues -> builder.withAllowedValues(
                allowedValues
                    .stream()
                    .map(value -> EnumVariableMember.create(value).build())
                    .collect(Collectors.toUnmodifiableList())));
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.ENUM;
    }

}
