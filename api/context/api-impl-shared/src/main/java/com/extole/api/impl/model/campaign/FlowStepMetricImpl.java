package com.extole.api.impl.model.campaign;

import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.campaign.AllFlowStepsBuildtimeContext;
import com.extole.api.model.campaign.FlowStepMetric;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;

final class FlowStepMetricImpl implements FlowStepMetric {
    private final com.extole.model.entity.campaign.CampaignFlowStepMetric campaignFlowStepMetric;

    FlowStepMetricImpl(com.extole.model.entity.campaign.CampaignFlowStepMetric campaignFlowStepMetric) {
        this.campaignFlowStepMetric = campaignFlowStepMetric;
    }

    @Override
    public String getId() {
        return campaignFlowStepMetric.getId().getValue();
    }

    @Override
    public BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> getName() {
        return campaignFlowStepMetric.getName();
    }

    @Override
    public BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> getDescription() {
        return Evaluatables.remapClassToClass(campaignFlowStepMetric.getDescription(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> getExpression() {
        return campaignFlowStepMetric.getExpression();
    }

    @Override
    public BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>> getTags() {
        return campaignFlowStepMetric.getTags();
    }

    @Override
    public BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> getUnit() {
        return campaignFlowStepMetric.getUnit();
    }
}
