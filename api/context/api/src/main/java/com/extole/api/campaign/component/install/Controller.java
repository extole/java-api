package com.extole.api.campaign.component.install;

import com.extole.api.campaign.component.install.step.action.Action;
import com.extole.api.campaign.component.install.step.data.StepData;

public interface Controller {

    String getId();

    String getName();

    String[] getAliases();

    Action[] getActions();

    void anchor(Action action);

    void anchor(Trigger trigger);

    void anchor(StepData trigger);

}
