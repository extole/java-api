package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentComponentIdVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.ComponentIdVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class ComponentIdVariableCreateRequestMapper
    implements SettingCreateRequestMapper<CampaignComponentComponentIdVariableRequest, ComponentIdVariableBuilder> {

    @Override
    public void complete(CampaignComponentComponentIdVariableRequest createRequest,
        ComponentIdVariableBuilder builder) throws VariableValueKeyLengthException {
        createRequest.getFilter().ifPresent(filter -> builder.withFilter()
            .withComponentTypes(filter.componentTypes()));
        createRequest.getValues().ifPresent(builder::withValues);
        createRequest.getSource().ifPresent(source -> builder.withSource(VariableSource.valueOf(source.name())));
        createRequest.getDescription().ifPresent(builder::withDescription);
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.COMPONENT_ID);
    }
}
