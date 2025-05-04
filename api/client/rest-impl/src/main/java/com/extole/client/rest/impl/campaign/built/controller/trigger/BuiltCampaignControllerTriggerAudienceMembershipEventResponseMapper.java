package com.extole.client.rest.impl.campaign.built.controller.trigger;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerAudienceMembershipEventResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.audience.membership.event.CampaignControllerTriggerAudienceMembershipEventType;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerAudienceMembershipEvent;

@Component
public class BuiltCampaignControllerTriggerAudienceMembershipEventResponseMapper implements
    BuiltCampaignControllerTriggerResponseMapper<BuiltCampaignControllerTriggerAudienceMembershipEvent,
        BuiltCampaignControllerTriggerAudienceMembershipEventResponse> {

    @Override
    public BuiltCampaignControllerTriggerAudienceMembershipEventResponse toResponse(
        BuiltCampaignControllerTriggerAudienceMembershipEvent trigger,
        ZoneId timeZone) {
        return new BuiltCampaignControllerTriggerAudienceMembershipEventResponse(
            trigger.getId().getValue(),
            CampaignControllerTriggerPhase.valueOf(trigger.getPhase().name()),
            trigger.getName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getEventTypes().stream()
                .map(item -> CampaignControllerTriggerAudienceMembershipEventType.valueOf(item.name()))
                .collect(Collectors.toSet()),
            trigger.getAudienceIds(),
            trigger.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            trigger.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.AUDIENCE_MEMBERSHIP_EVENT;
    }

}
