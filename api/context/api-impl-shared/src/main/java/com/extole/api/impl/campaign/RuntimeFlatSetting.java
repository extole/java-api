package com.extole.api.impl.campaign;

import java.util.Optional;

import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;
import com.extole.running.service.campaign.RunningCampaign;
import com.extole.running.service.component.RunningComponent;

public interface RuntimeFlatSetting {

    RuntimeFlatVariableType getType();

    String getName();

    VariableKey getVariableKey();

    RuntimeEvaluatable<Object, Optional<Object>> getValue();

    Id<RunningComponent> getComponentId();

    RunningCampaign getCampaign();
}
