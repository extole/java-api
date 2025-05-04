package com.extole.api.impl.campaign.component.install.step.action;

import com.extole.api.campaign.component.install.step.action.Action;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerAction;

final class ActionImpl implements Action {

    private final String id;
    private final String type;

    ActionImpl(BuiltCampaignControllerAction action) {
        this.id = action.getId().getValue();
        this.type = action.getType().name();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

}
