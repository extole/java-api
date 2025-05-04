package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.revoke.reward.BuiltCampaignControllerActionRevokeRewardResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionRevokeReward;

@Component
public class BuiltCampaignControllerActionRevokeRewardResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<
        BuiltCampaignControllerActionRevokeReward,
        BuiltCampaignControllerActionRevokeRewardResponse> {

    @Override
    public BuiltCampaignControllerActionRevokeRewardResponse
        toResponse(BuiltCampaignControllerActionRevokeReward action, ZoneId timeZone) {
        return new BuiltCampaignControllerActionRevokeRewardResponse(
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
            action.getMessage());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.REVOKE_REWARD;
    }

}
