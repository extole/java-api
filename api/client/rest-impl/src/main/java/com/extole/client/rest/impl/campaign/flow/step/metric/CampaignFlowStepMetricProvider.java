package com.extole.client.rest.impl.campaign.flow.step.metric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.flow.step.CampaignFlowStepRestException;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricRestException;
import com.extole.client.rest.impl.campaign.flow.step.CampaignFlowStepProvider;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignFlowStepMetric;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStepMetric;

@Component
public final class CampaignFlowStepMetricProvider {

    private final CampaignFlowStepProvider campaignFlowStepProvider;

    @Autowired
    public CampaignFlowStepMetricProvider(CampaignFlowStepProvider campaignFlowStepProvider) {
        this.campaignFlowStepProvider = campaignFlowStepProvider;
    }

    public CampaignFlowStepMetric getCampaignFlowStepMetric(Campaign campaign, String flowStepId,
        String flowStepMetricId)
        throws CampaignFlowStepMetricRestException, CampaignFlowStepRestException {
        return campaignFlowStepProvider.getCampaignFlowStep(campaign, flowStepId).getMetrics()
            .stream()
            .filter(flowStepMetric -> flowStepMetric.getId().equals(Id.valueOf(flowStepMetricId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignFlowStepMetricRestException.class)
                .withErrorCode(CampaignFlowStepMetricRestException.INVALID_CAMPAIGN_FLOW_STEP_METRIC_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("flow_step_id", flowStepId)
                .addParameter("flow_step_metric_id", flowStepMetricId)
                .build());
    }

    public BuiltCampaignFlowStepMetric getBuiltCampaignFlowStepMetric(BuiltCampaign campaign, String flowStepId,
        String flowStepMetricId)
        throws CampaignFlowStepMetricRestException, CampaignFlowStepRestException {
        return campaignFlowStepProvider.getBuiltCampaignFlowStep(campaign, flowStepId).getMetrics()
            .stream()
            .filter(flowStepMetric -> flowStepMetric.getId().equals(Id.valueOf(flowStepMetricId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignFlowStepMetricRestException.class)
                .withErrorCode(CampaignFlowStepMetricRestException.INVALID_CAMPAIGN_FLOW_STEP_METRIC_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("flow_step_id", flowStepId)
                .addParameter("flow_step_metric_id", flowStepMetricId)
                .build());
    }
}
