package com.extole.api.impl.model.campaign.built;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.built.BuiltCampaignFlowStepMetric;

final class BuiltCampaignFlowStepMetricImpl implements BuiltCampaignFlowStepMetric {
    private final com.extole.model.entity.campaign.built.BuiltCampaignFlowStepMetric flowStepMetric;

    BuiltCampaignFlowStepMetricImpl(com.extole.model.entity.campaign.built.BuiltCampaignFlowStepMetric flowStepMetric) {
        this.flowStepMetric = flowStepMetric;
    }

    @Override
    public String getId() {
        return flowStepMetric.getId().getValue();
    }

    @Override
    public String getName() {
        return flowStepMetric.getName();
    }

    @Nullable
    @Override
    public String getDescription() {
        return flowStepMetric.getDescription().orElse(null);
    }

    @Override
    public String getExpression() {
        return flowStepMetric.getExpression();
    }

    @Override
    public String[] getTags() {
        return flowStepMetric.getTags().toArray(String[]::new);
    }

    @Override
    public String getUnit() {
        return flowStepMetric.getUnit();
    }
}
