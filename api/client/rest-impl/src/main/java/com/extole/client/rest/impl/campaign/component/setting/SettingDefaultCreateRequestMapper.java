package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.VariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class SettingDefaultCreateRequestMapper
    implements SettingCreateRequestMapper<CampaignComponentVariableRequest, VariableBuilder> {

    @Override
    public void complete(CampaignComponentVariableRequest createRequest, VariableBuilder builder)
        throws VariableValueKeyLengthException {
        createRequest.getValues().ifPresent(values -> {
            builder.withValues(values);
        });
        createRequest.getSource().ifPresent(source -> {
            builder.withSource(VariableSource.valueOf(source.name()));
        });
        createRequest.getDescription().ifPresent(description -> {
            builder.withDescription(description);
        });
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.STRING);
    }
}
