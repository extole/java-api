package com.extole.api.impl.model.campaign.built;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.built.BuiltCampaignFlowStep;
import com.extole.api.model.campaign.built.BuiltCampaignFlowStepApp;
import com.extole.api.model.campaign.built.BuiltCampaignFlowStepMetric;
import com.extole.api.model.campaign.built.BuiltCampaignFlowStepWords;

final class BuiltCampaignFlowStepImpl implements BuiltCampaignFlowStep {
    private final com.extole.model.entity.campaign.built.BuiltCampaignFlowStep flowStep;

    BuiltCampaignFlowStepImpl(com.extole.model.entity.campaign.built.BuiltCampaignFlowStep flowStep) {
        this.flowStep = flowStep;
    }

    @Override
    public String getId() {
        return flowStep.getId().getValue();
    }

    @Override
    public String getFlowPath() {
        return flowStep.getFlowPath();
    }

    @Override
    public BigDecimal getSequence() {
        return flowStep.getSequence();
    }

    @Override
    public String getStepName() {
        return flowStep.getStepName();
    }

    @Override
    public String getIconType() {
        return flowStep.getIconType();
    }

    @Override
    public BuiltCampaignFlowStepMetric[] getMetrics() {
        return flowStep.getMetrics().stream()
            .map(value -> new BuiltCampaignFlowStepMetricImpl(value))
            .toArray(BuiltCampaignFlowStepMetric[]::new);
    }

    @Override
    public BuiltCampaignFlowStepApp[] getApps() {
        return flowStep.getApps().stream()
            .map(value -> new BuiltCampaignFlowStepAppImpl(value))
            .toArray(BuiltCampaignFlowStepApp[]::new);
    }

    @Override
    public String[] getTags() {
        return flowStep.getTags().toArray(String[]::new);
    }

    @Override
    public String getName() {
        return flowStep.getName();
    }

    @Override
    public String getIconColor() {
        return flowStep.getIconColor();
    }

    @Nullable
    @Override
    public String getDescription() {
        return flowStep.getDescription().orElse(null);
    }

    @Override
    public BuiltCampaignFlowStepWords getWords() {
        return new BuiltCampaignFlowStepWordsImpl(flowStep.getWords());
    }

    @Override
    public String getCreatedDate() {
        return flowStep.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return flowStep.getUpdatedDate().toString();
    }
}
