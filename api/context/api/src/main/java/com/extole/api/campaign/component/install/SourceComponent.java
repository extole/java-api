package com.extole.api.campaign.component.install;

import com.extole.api.campaign.component.install.step.action.Action;
import com.extole.api.campaign.component.install.step.data.StepData;

public interface SourceComponent {

    Trigger[] getTriggers();

    Trigger[] getUnanchoredTriggers();

    Action[] getActions();

    Action[] getUnanchoredActions();

    StepData[] getStepData();

    StepData[] getUnanchoredStepData();

}
