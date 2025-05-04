package com.extole.client.rest.impl.campaign.controller.trigger.audience.membership.event;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerAudienceMembershipEventConfiguration;
import com.extole.client.rest.campaign.controller.trigger.audience.membership.event.CampaignControllerTriggerAudienceMembershipEventResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTriggerAudienceMembershipEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class CampaignControllerTriggerAudienceMembershipEventResponseMapper implements
    CampaignControllerTriggerResponseMapper<
        CampaignControllerTriggerAudienceMembershipEvent,
        CampaignControllerTriggerAudienceMembershipEventResponse,
        CampaignControllerTriggerAudienceMembershipEventConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerTriggerAudienceMembershipEventResponseMapper(
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerTriggerAudienceMembershipEventResponse toResponse(
        CampaignControllerTriggerAudienceMembershipEvent trigger,
        ZoneId timeZone) {
        return new CampaignControllerTriggerAudienceMembershipEventResponse(trigger.getId().getValue(),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            Evaluatables.remapEnumCollection(trigger.getEventTypes(), new TypeReference<>() {}),
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
    public CampaignControllerTriggerAudienceMembershipEventConfiguration toConfiguration(
        CampaignControllerTriggerAudienceMembershipEvent trigger, ZoneId timeZone,
        Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerTriggerAudienceMembershipEventConfiguration(
            Omissible.of(Id.valueOf(trigger.getId().getValue())),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            Evaluatables.remapEnumCollection(trigger.getEventTypes(), new TypeReference<>() {}),
            trigger.getAudienceIds(),
            trigger.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.AUDIENCE_MEMBERSHIP_EVENT;
    }

}
