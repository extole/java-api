package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.decline.BuiltCampaignControllerActionDeclineResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionDecline;

@Component
public class BuiltCampaignControllerActionDeclineResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<BuiltCampaignControllerActionDecline,
        BuiltCampaignControllerActionDeclineResponse> {

    @Override
    public BuiltCampaignControllerActionDeclineResponse toResponse(BuiltCampaignControllerActionDecline action,
        ZoneId timeZone) {
        return new BuiltCampaignControllerActionDeclineResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getLegacyActionId(),
            action.getPartnerEventId(),
            action.getEventType(),
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
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.DECLINE;
    }

}
