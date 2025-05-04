package com.extole.api.impl.model.campaign;

import java.math.BigDecimal;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.model.campaign.FlowStep;
import com.extole.api.model.campaign.FlowStepApp;
import com.extole.api.model.campaign.FlowStepMetric;
import com.extole.api.model.campaign.FlowStepWords;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;

public final class FlowStepImpl implements FlowStep {
    private final com.extole.model.entity.campaign.CampaignFlowStep campaignFlowStep;

    public FlowStepImpl(com.extole.model.entity.campaign.CampaignFlowStep campaignFlowStep) {
        this.campaignFlowStep = campaignFlowStep;
    }

    @Override
    public String getId() {
        return campaignFlowStep.getId().getValue();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getFlowPath() {
        return campaignFlowStep.getFlowPath();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> getSequence() {
        return campaignFlowStep.getSequence();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getStepName() {
        return campaignFlowStep.getStepName();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getIconType() {
        return campaignFlowStep.getIconType();
    }

    @Override
    public FlowStepMetric[] getMetrics() {
        return campaignFlowStep.getMetrics().stream()
            .map(value -> new FlowStepMetricImpl(value))
            .toArray(FlowStepMetric[]::new);
    }

    @Override
    public FlowStepApp[] getApps() {
        return campaignFlowStep.getApps().stream()
            .map(value -> new FlowStepAppImpl(value))
            .toArray(FlowStepApp[]::new);
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getTags() {
        return campaignFlowStep.getTags();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return campaignFlowStep.getName();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getIconColor() {
        return campaignFlowStep.getIconColor();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getDescription() {
        return Evaluatables.remapClassToClass(campaignFlowStep.getDescription(), new TypeReference<>() {});
    }

    @Override
    public FlowStepWords getWords() {
        return new FlowStepWordsImpl(campaignFlowStep.getWords());
    }

    @Override
    public String getCreatedDate() {
        return campaignFlowStep.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return campaignFlowStep.getUpdatedDate().toString();
    }
}
