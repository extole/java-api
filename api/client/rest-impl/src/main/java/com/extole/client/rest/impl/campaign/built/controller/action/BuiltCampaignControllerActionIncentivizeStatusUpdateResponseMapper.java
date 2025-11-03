package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.incentivize.status.update.BuiltCampaignControllerActionIncentivizeStatusUpdateResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionIncentivizeStatusUpdate;

@Component
public class BuiltCampaignControllerActionIncentivizeStatusUpdateResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<BuiltCampaignControllerActionIncentivizeStatusUpdate,
        BuiltCampaignControllerActionIncentivizeStatusUpdateResponse> {

    @Override
    public BuiltCampaignControllerActionIncentivizeStatusUpdateResponse toResponse(
        BuiltCampaignControllerActionIncentivizeStatusUpdate action,
        ZoneId timeZone) {

        return new BuiltCampaignControllerActionIncentivizeStatusUpdateResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toUnmodifiableList()),
            action.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            action.getLegacyActionId(),
            Evaluatables.remapOptional(action.getEventType(), new TypeReference<>() {}),
            action.getPartnerEventId(),
            action.getReviewStatus(),
            action.getMessage(),
            action.isMoveToPending(),
            action.getData());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.INCENTIVIZE_STATUS_UPDATE;
    }

}
