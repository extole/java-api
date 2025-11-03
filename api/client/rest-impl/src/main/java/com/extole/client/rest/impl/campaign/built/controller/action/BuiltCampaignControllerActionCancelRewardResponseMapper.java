package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.cancel.reward.BuiltCampaignControllerActionCancelRewardResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionCancelReward;

@Component
public class BuiltCampaignControllerActionCancelRewardResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<BuiltCampaignControllerActionCancelReward,
        BuiltCampaignControllerActionCancelRewardResponse> {

    @Override
    public BuiltCampaignControllerActionCancelRewardResponse
        toResponse(BuiltCampaignControllerActionCancelReward action, ZoneId timeZone) {
        return new BuiltCampaignControllerActionCancelRewardResponse(
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
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.CANCEL_REWARD;
    }

}
