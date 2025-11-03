package com.extole.api.step.campaign.step;

import com.extole.api.step.campaign.step.data.StepData;
import com.extole.api.step.campaign.step.trigger.StepTrigger;

public interface Step {

    enum StepType {

        FRONTEND_CONTROLLER,
        BACKEND_CONTROLLER,
        JOURNEY_ENTRY

    }

    String getId();

    String getName();

    String getType();

    String getJourneyName();

    StepTrigger[] getTriggers();

    StepData[] getData();

}
