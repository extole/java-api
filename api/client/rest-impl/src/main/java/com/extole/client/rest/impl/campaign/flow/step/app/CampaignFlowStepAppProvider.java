package com.extole.client.rest.impl.campaign.flow.step.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.flow.step.CampaignFlowStepRestException;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppRestException;
import com.extole.client.rest.impl.campaign.flow.step.CampaignFlowStepProvider;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignFlowStepApp;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStepApp;

@Component
public final class CampaignFlowStepAppProvider {

    private final CampaignFlowStepProvider campaignFlowStepProvider;

    @Autowired
    CampaignFlowStepAppProvider(CampaignFlowStepProvider campaignFlowStepProvider) {
        this.campaignFlowStepProvider = campaignFlowStepProvider;
    }

    public CampaignFlowStepApp getCampaignFlowStepApp(Campaign campaign, String flowStepId, String flowStepAppId)
        throws CampaignFlowStepAppRestException, CampaignFlowStepRestException {
        return campaignFlowStepProvider.getCampaignFlowStep(campaign, flowStepId).getApps()
            .stream()
            .filter(flowStepApp -> flowStepApp.getId().equals(Id.valueOf(flowStepAppId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignFlowStepAppRestException.class)
                .withErrorCode(CampaignFlowStepAppRestException.INVALID_CAMPAIGN_FLOW_STEP_APP_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("flow_step_id", flowStepId)
                .addParameter("flow_step_app_id", flowStepAppId)
                .build());
    }

    public BuiltCampaignFlowStepApp getBuiltCampaignFlowStepApp(BuiltCampaign campaign, String flowStepId,
        String flowStepAppId)
        throws CampaignFlowStepAppRestException, CampaignFlowStepRestException {
        return campaignFlowStepProvider.getBuiltCampaignFlowStep(campaign, flowStepId).getApps()
            .stream()
            .filter(flowStepApp -> flowStepApp.getId().equals(Id.valueOf(flowStepAppId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignFlowStepAppRestException.class)
                .withErrorCode(CampaignFlowStepAppRestException.INVALID_CAMPAIGN_FLOW_STEP_APP_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("flow_step_id", flowStepId)
                .addParameter("flow_step_app_id", flowStepAppId)
                .build());
    }

}
