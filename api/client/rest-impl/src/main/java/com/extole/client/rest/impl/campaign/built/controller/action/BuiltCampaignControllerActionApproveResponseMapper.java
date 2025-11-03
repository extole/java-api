package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.approve.BuiltCampaignControllerActionApproveResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionApprove;

@Component
public class BuiltCampaignControllerActionApproveResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<BuiltCampaignControllerActionApprove,
        BuiltCampaignControllerActionApproveResponse> {

    @Override
    public BuiltCampaignControllerActionApproveResponse toResponse(BuiltCampaignControllerActionApprove action,
        ZoneId timeZone) {
        return new BuiltCampaignControllerActionApproveResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getLegacyActionId(),
            action.getPartnerEventId(),
            action.getEventType(),
            action.getForce(),
            action.getNote(),
            action.getCauseType(),
            action.getPollingId(),
            action.getPollingName(),
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
            action.getRewardTags());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.APPROVE;
    }

}
