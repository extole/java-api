package com.extole.api.impl.model.campaign;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.model.campaign.ControllerAction;
import com.extole.evaluateable.BuildtimeEvaluatable;

final class ControllerActionImpl implements ControllerAction {
    private final com.extole.model.entity.campaign.CampaignControllerAction action;

    ControllerActionImpl(com.extole.model.entity.campaign.CampaignControllerAction action) {
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
    public String getQuality() {
        return action.getQuality().name();
    }

    @Override
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> getEnabled() {
        return action.getEnabled();
    }

    @Override
    public String getCreatedDate() {
        return action.getCreatedDate().toString();
    }
}
