package com.extole.api.impl.campaign;

import com.extole.id.Id;
import com.extole.running.service.campaign.RunningCampaign;
import com.extole.running.service.component.RunningComponent;
import com.extole.running.service.component.RunningSetting;

@FunctionalInterface
public interface FlatSettingMapper {
    RuntimeFlatSetting map(RunningSetting variable, Id<RunningComponent> componentId, RunningCampaign runningCampaign);
}
