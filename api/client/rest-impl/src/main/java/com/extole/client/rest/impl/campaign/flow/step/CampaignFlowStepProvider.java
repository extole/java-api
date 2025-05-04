package com.extole.client.rest.impl.campaign.flow.step;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.flow.step.CampaignFlowStepRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignFlowStep;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStep;

@Component
public final class CampaignFlowStepProvider {

    public CampaignFlowStep getCampaignFlowStep(Campaign campaign, String flowStepId)
        throws CampaignFlowStepRestException {
        return campaign.getFlowSteps().stream()
            .filter(flowStep -> flowStep.getId().equals(Id.valueOf(flowStepId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignFlowStepRestException.class)
                .withErrorCode(CampaignFlowStepRestException.INVALID_CAMPAIGN_FLOW_STEP_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("flow_step_id", flowStepId)
                .build());
    }

    public BuiltCampaignFlowStep getBuiltCampaignFlowStep(BuiltCampaign campaign, String flowStepId)
        throws CampaignFlowStepRestException {
        return campaign.getFlowSteps().stream()
            .filter(flowStep -> flowStep.getId().equals(Id.valueOf(flowStepId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignFlowStepRestException.class)
                .withErrorCode(CampaignFlowStepRestException.INVALID_CAMPAIGN_FLOW_STEP_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("flow_step_id", flowStepId)
                .build());
    }

}
