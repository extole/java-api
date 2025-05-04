package com.extole.client.rest.impl.campaign.component;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignComponent;

@Component
public final class CampaignComponentProvider {

    public CampaignComponent getCampaignComponent(String componentId, Campaign campaign)
        throws CampaignComponentRestException {
        return campaign.getComponents()
            .stream()
            .filter(campaignComponentCandidate -> campaignComponentCandidate.getId().getValue().equals(componentId))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                .withErrorCode(CampaignComponentRestException.CAMPAIGN_COMPONENT_NOT_FOUND)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("campaign_component_id", componentId)
                .build());
    }

    public BuiltCampaignComponent getBuiltCampaignComponent(String componentId, BuiltCampaign campaign)
        throws CampaignComponentRestException {
        return campaign.getComponents()
            .stream()
            .filter(campaignComponentCandidate -> campaignComponentCandidate.getId().getValue().equals(componentId))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                .withErrorCode(CampaignComponentRestException.CAMPAIGN_COMPONENT_NOT_FOUND)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("campaign_component_id", componentId)
                .build());
    }
}
