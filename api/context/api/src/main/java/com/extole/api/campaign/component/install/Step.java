package com.extole.api.campaign.component.install;

import com.extole.api.campaign.component.install.step.data.StepData;

public interface Step {
    enum StepType {
        FRONTEND_CONTROLLER,
        CONTROLLER,
        JOURNEY_ENTRY,
    }

    String getId();

    String getType();

    String getName();

    void anchor(Trigger trigger);

    void anchor(StepData stepData);

}
