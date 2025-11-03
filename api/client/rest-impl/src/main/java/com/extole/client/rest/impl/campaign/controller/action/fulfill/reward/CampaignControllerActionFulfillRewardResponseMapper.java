package com.extole.client.rest.impl.campaign.controller.action.fulfill.reward;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionFulfillRewardConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.fulfill.reward.CampaignControllerActionFulfillRewardResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionFulfillReward;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionFulfillRewardResponseMapper implements
    CampaignControllerActionResponseMapper<CampaignControllerActionFulfillReward,
        CampaignControllerActionFulfillRewardResponse, CampaignControllerActionFulfillRewardConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerActionFulfillRewardResponseMapper(
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerActionFulfillRewardResponse
        toResponse(CampaignControllerActionFulfillReward action, ZoneId timeZone) {
        return new CampaignControllerActionFulfillRewardResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            action.getRewardId(),
            action.getMessage(),
            action.getSuccess(),
            action.getPartnerRewardId(),
            action.getEventTime());
    }

    @Override
    public CampaignControllerActionFulfillRewardConfiguration
        toConfiguration(CampaignControllerActionFulfillReward action, ZoneId timeZone,
            Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerActionFulfillRewardConfiguration(
            Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()),
            action.getRewardId(),
            action.getMessage(),
            action.getSuccess(),
            action.getPartnerRewardId(),
            action.getEventTime());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.FULFILL_REWARD;
    }

}
