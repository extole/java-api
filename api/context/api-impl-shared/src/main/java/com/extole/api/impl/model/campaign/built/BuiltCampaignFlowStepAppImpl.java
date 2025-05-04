package com.extole.api.impl.model.campaign.built;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.built.BuiltCampaignFlowStepApp;

final class BuiltCampaignFlowStepAppImpl implements BuiltCampaignFlowStepApp {
    private final com.extole.model.entity.campaign.built.BuiltCampaignFlowStepApp flowStepApp;

    BuiltCampaignFlowStepAppImpl(com.extole.model.entity.campaign.built.BuiltCampaignFlowStepApp flowStepApp) {
        this.flowStepApp = flowStepApp;
    }

    @Override
    public String getId() {
        return flowStepApp.getId().getValue();
    }

    @Override
    public String getName() {
        return flowStepApp.getName();
    }

    @Nullable
    @Override
    public String getDescription() {
        return flowStepApp.getDescription().orElse(null);
    }

    @Override
    public String getType() {
        return flowStepApp.getType().getName();
    }
}
