package com.extole.api.impl.model.campaign;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.model.campaign.FlowStepAppType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;

final class FlowStepAppTypeImpl implements FlowStepAppType {
    private final com.extole.model.entity.campaign.CampaignFlowStepAppType campaignFlowStepAppType;

    FlowStepAppTypeImpl(com.extole.model.entity.campaign.CampaignFlowStepAppType campaignFlowStepAppType) {
        this.campaignFlowStepAppType = campaignFlowStepAppType;
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return Evaluatables.remapClassToClass(campaignFlowStepAppType.getName(), new TypeReference<>() {});
    }
}
