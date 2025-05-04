package com.extole.client.rest.impl.campaign.component.setting;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentEnumVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.EnumVariableMember;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.EnumVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class EnumVariableCreateRequestMapper
    implements SettingCreateRequestMapper<CampaignComponentEnumVariableRequest, EnumVariableBuilder> {

    @Override
    public void complete(CampaignComponentEnumVariableRequest createRequest, EnumVariableBuilder builder)
        throws VariableValueKeyLengthException {
        createRequest.getValues().ifPresent(values -> builder.withValues(values));
        createRequest.getSource().ifPresent(source -> builder.withSource(VariableSource.valueOf(source.name())));
        createRequest.getDescription().ifPresent(description -> builder.withDescription(description));
        createRequest.getAllowedValues().ifPresent(
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
