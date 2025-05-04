package com.extole.api.impl.campaign.component.install.step.action;

import com.extole.api.campaign.component.install.step.action.FireAsPersonAction;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionFireAsPerson;

final class FireAsPersonActionImpl implements FireAsPersonAction {

    private final BuiltCampaignControllerActionFireAsPerson action;

    FireAsPersonActionImpl(BuiltCampaignControllerActionFireAsPerson action) {
        this.action = action;
    }

    @Override
    public String getId() {
        return action.getId().getValue();
    }

    @Override
    public String getType() {
        return action.getType().name();
    }

    @Override
    public String getEventName() {
        return action.getEventName();
    }

}
