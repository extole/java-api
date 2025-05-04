package com.extole.client.rest.impl.campaign.built.controller;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.BuiltCampaignJourneyEntryResponse;
import com.extole.client.rest.campaign.built.controller.BuiltJourneyKeyResponse;
import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponseMapper;
import com.extole.client.rest.impl.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponseMapperRepository;
import com.extole.id.Id;
import com.extole.model.entity.campaign.StepType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTrigger;
import com.extole.model.entity.campaign.built.BuiltCampaignJourneyEntry;

@Component
public class BuiltCampaignJourneyEntryResponseMapper
    implements BuiltCampaignStepResponseMapper<BuiltCampaignJourneyEntry, BuiltCampaignJourneyEntryResponse> {

    private final BuiltCampaignControllerTriggerResponseMapperRepository triggerMapperRepository;

    @Autowired
    public BuiltCampaignJourneyEntryResponseMapper(
        BuiltCampaignControllerTriggerResponseMapperRepository triggerMapperRepository) {
        this.triggerMapperRepository = triggerMapperRepository;
    }

    @Override
    public StepType getStepType() {
        return StepType.JOURNEY_ENTRY;
    }

    @Override
    public BuiltCampaignJourneyEntryResponse toResponse(BuiltCampaignJourneyEntry journeyEntry, ZoneId timeZone) {

        return new BuiltCampaignJourneyEntryResponse(
            journeyEntry.getId().getValue(),
            journeyEntry.isEnabled(),
            journeyEntry.getTriggers()
                .stream()
                .map(trigger -> toTriggerResponse(trigger, timeZone))
                .collect(Collectors.toList()),
            journeyEntry.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            journeyEntry.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            journeyEntry.getCreatedDate().atZone(timeZone),
            journeyEntry.getUpdatedDate().atZone(timeZone),
            journeyEntry.getJourneyName().getValue(),
            journeyEntry.getPriority(),
            journeyEntry.getKey().map(value -> new BuiltJourneyKeyResponse(value.getName(), value.getValue())));
    }

    private BuiltCampaignControllerTriggerResponse toTriggerResponse(BuiltCampaignControllerTrigger trigger,
        ZoneId timeZone) {
        BuiltCampaignControllerTriggerResponseMapper mapper = triggerMapperRepository.getMapper(trigger.getType());
        return mapper.toResponse(trigger, timeZone);
    }

}
