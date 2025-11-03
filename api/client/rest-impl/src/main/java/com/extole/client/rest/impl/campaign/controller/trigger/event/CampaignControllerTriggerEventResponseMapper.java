package com.extole.client.rest.impl.campaign.controller.trigger.event;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerEventConfiguration;
import com.extole.client.rest.campaign.controller.trigger.event.CampaignControllerTriggerEventResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTriggerEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class CampaignControllerTriggerEventResponseMapper implements
    CampaignControllerTriggerResponseMapper<CampaignControllerTriggerEvent, CampaignControllerTriggerEventResponse,
        CampaignControllerTriggerEventConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerTriggerEventResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerTriggerEventResponse toResponse(CampaignControllerTriggerEvent trigger,
        ZoneId timeZone) {
        return new CampaignControllerTriggerEventResponse(trigger.getId().getValue(),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getEventNames(),
            Evaluatables.remapEnum(trigger.getEventType(), new TypeReference<>() {}),
            trigger.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            trigger.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerEventConfiguration toConfiguration(CampaignControllerTriggerEvent trigger,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerTriggerEventConfiguration(Omissible.of(Id.valueOf(trigger.getId().getValue())),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getEventNames(),
            Evaluatables.remapEnum(trigger.getEventType(), new TypeReference<>() {}),
            trigger.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.EVENT;
    }

}
