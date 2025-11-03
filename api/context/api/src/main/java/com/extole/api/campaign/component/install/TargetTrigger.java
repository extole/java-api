package com.extole.api.campaign.component.install;

public interface TargetTrigger extends Trigger {

    String getId();

    String getName();

    String getType();

    Step getStep();
}
