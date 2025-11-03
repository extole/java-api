package com.extole.client.rest.impl.campaign.controller.action.revoke.reward;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionRevokeRewardConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.revoke.reward.CampaignControllerActionRevokeRewardResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionRevokeReward;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionRevokeRewardResponseMapper implements
    CampaignControllerActionResponseMapper<CampaignControllerActionRevokeReward,
        CampaignControllerActionRevokeRewardResponse, CampaignControllerActionRevokeRewardConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerActionRevokeRewardResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerActionRevokeRewardResponse
        toResponse(CampaignControllerActionRevokeReward action, ZoneId timeZone) {
        return new CampaignControllerActionRevokeRewardResponse(
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
            action.getMessage());
    }

    @Override
    public CampaignControllerActionRevokeRewardConfiguration
        toConfiguration(CampaignControllerActionRevokeReward action, ZoneId timeZone,
            Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerActionRevokeRewardConfiguration(
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
            action.getMessage());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.REVOKE_REWARD;
    }

}
