package com.extole.api.campaign.component.install;

import javax.annotation.Nullable;

public interface TargetComponent {

    @Nullable
    TargetComponent getParent();

    TargetTrigger[] getTriggers();

    Controller[] getControllers();

    FrontendController[] getFrontendControllers();

    JourneyEntry[] getJourneyEntries();

    Step[] getSteps();

    Object getVariableValue(String name);

    Object getVariableValue(String name, String... keys);
}
