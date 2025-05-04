package com.extole.api.impl.campaign;

import com.extole.id.Id;
import com.extole.running.service.campaign.RunningCampaign;
import com.extole.running.service.component.RunningComponent;
import com.extole.running.service.component.RunningVariable;

@FunctionalInterface
public interface FlatVariableMapper {
    RuntimeFlatSetting map(RunningVariable variable, String variableVariant, Id<RunningComponent> componentId,
        RunningCampaign runningCampaign);
}
