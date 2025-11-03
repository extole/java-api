package com.extole.api.campaign.component.install;

import com.extole.api.campaign.component.install.step.action.Action;

public interface Controller extends Step {

    String[] getAliases();

    Action[] getActions();

    void anchor(Action action);

}
