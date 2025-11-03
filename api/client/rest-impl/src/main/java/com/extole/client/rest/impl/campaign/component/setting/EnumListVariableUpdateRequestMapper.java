package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentEnumListVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.EnumVariableMember;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.EnumVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
final class EnumListVariableUpdateRequestMapper
    implements SettingUpdateRequestMapper<CampaignComponentEnumListVariableUpdateRequest, EnumVariableBuilder> {

    @Override
    public void complete(
        CampaignComponentEnumListVariableUpdateRequest updateRequest, EnumVariableBuilder builder)
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
                    .toList()));
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.ENUM_LIST);
    }

}
