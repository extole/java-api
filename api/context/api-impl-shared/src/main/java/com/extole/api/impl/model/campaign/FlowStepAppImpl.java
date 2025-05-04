package com.extole.api.impl.model.campaign;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.model.campaign.FlowStepApp;
import com.extole.api.model.campaign.FlowStepAppType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;

final class FlowStepAppImpl implements FlowStepApp {
    private final com.extole.model.entity.campaign.CampaignFlowStepApp campaignFlowStepApp;

    FlowStepAppImpl(com.extole.model.entity.campaign.CampaignFlowStepApp campaignFlowStepApp) {
        this.campaignFlowStepApp = campaignFlowStepApp;
    }

    @Override
    public String getId() {
        return campaignFlowStepApp.getId().getValue();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return campaignFlowStepApp.getName();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getDescription() {
        return Evaluatables.remapClassToClass(campaignFlowStepApp.getDescription(), new TypeReference<>() {});
    }

    @Override
    public FlowStepAppType getType() {
        return new FlowStepAppTypeImpl(campaignFlowStepApp.getType());
    }
}
