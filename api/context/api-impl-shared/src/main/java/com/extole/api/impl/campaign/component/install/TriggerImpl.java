package com.extole.api.impl.campaign.component.install;

import com.extole.api.campaign.component.install.Trigger;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTrigger;

public class TriggerImpl implements Trigger {

    private final String id;
    private final String name;
    private final String type;

    public TriggerImpl(BuiltCampaignControllerTrigger trigger) {
        this.id = trigger.getId().getValue();
        this.name = trigger.getName();
        this.type = trigger.getType().name();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

}
