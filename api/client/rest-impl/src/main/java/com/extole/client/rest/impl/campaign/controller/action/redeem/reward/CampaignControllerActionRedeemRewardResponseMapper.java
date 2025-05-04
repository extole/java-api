package com.extole.client.rest.impl.campaign.controller.action.redeem.reward;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionRedeemRewardConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionRedeemReward;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionRedeemRewardResponseMapper implements
    CampaignControllerActionResponseMapper<
        CampaignControllerActionRedeemReward,
        CampaignControllerActionRedeemRewardResponse,
        CampaignControllerActionRedeemRewardConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerActionRedeemRewardResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerActionRedeemRewardResponse
        toResponse(CampaignControllerActionRedeemReward action, ZoneId timeZone) {
        return new CampaignControllerActionRedeemRewardResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getEnabled(),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            action.getRewardId(),
            action.getData(),
            action.getPartnerEventId(),
            action.getEventTime());
    }

    @Override
    public CampaignControllerActionRedeemRewardConfiguration
        toConfiguration(CampaignControllerActionRedeemReward action, ZoneId timeZone,
            Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerActionRedeemRewardConfiguration(
            Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            action.getEnabled(),
            action.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()),
            action.getRewardId(),
            action.getData(),
            action.getPartnerEventId(),
            action.getEventTime());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.REDEEM_REWARD;
    }

}
