package com.extole.api.step.campaign.step;

import com.extole.api.step.campaign.step.action.StepAction;

public interface BackendStep extends Step {

    StepAction[] getActions();

    String[] getAliases();

}
