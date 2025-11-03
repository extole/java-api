package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentComponentIdVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.ComponentIdFilterBuilder;
import com.extole.model.service.campaign.setting.ComponentIdVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class ComponentIdVariableUpdateRequestMapper
    implements
    SettingUpdateRequestMapper<CampaignComponentComponentIdVariableUpdateRequest, ComponentIdVariableBuilder> {

    @Override
    public void complete(CampaignComponentComponentIdVariableUpdateRequest updateRequest,
        ComponentIdVariableBuilder builder) throws VariableValueKeyLengthException {
        updateRequest.getValues().ifPresent(builder::withValues);
        updateRequest.getSource().ifPresent(source -> builder.withSource(VariableSource.valueOf(source.name())));
        updateRequest.getDescription().ifPresent(builder::withDescription);
        updateRequest.getFilter().ifPresent(filter -> {
            ComponentIdFilterBuilder filterBuilder = builder.withFilter();
            filter.componentTypes().ifPresent(componentTypes -> filterBuilder.withComponentTypes(componentTypes));
        });
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.COMPONENT_ID);
    }
}
