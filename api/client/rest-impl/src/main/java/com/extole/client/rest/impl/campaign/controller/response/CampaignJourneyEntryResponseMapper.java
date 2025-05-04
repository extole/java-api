package com.extole.client.rest.impl.campaign.controller.response;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignJourneyEntryConfiguration;
import com.extole.client.rest.campaign.configuration.JourneyKeyConfiguration;
import com.extole.client.rest.campaign.configuration.StepDataConfiguration;
import com.extole.client.rest.campaign.controller.response.CampaignJourneyEntryResponse;
import com.extole.client.rest.campaign.controller.response.JourneyKeyResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.step.data.StepDataResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapperRepository;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTrigger;
import com.extole.model.entity.campaign.CampaignJourneyEntry;
import com.extole.model.entity.campaign.StepData;
import com.extole.model.entity.campaign.StepType;

@Component
public class CampaignJourneyEntryResponseMapper implements
    CampaignStepResponseMapper<CampaignJourneyEntry, CampaignJourneyEntryResponse, CampaignJourneyEntryConfiguration> {

    private final CampaignControllerTriggerResponseMapperRepository triggerMapperRepository;
    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignJourneyEntryResponseMapper(
        CampaignControllerTriggerResponseMapperRepository triggerMapperRepository,
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.triggerMapperRepository = triggerMapperRepository;
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public StepType getStepType() {
        return StepType.JOURNEY_ENTRY;
    }

    @Override
    public CampaignJourneyEntryResponse toResponse(CampaignJourneyEntry journeyEntry, ZoneId timeZone) {

        return new CampaignJourneyEntryResponse(
            journeyEntry.getId().getValue(),
            journeyEntry.getEnabled(),
            journeyEntry.getTriggers()
                .stream()
                .map(trigger -> toTriggerResponse(trigger, timeZone))
                .collect(Collectors.toUnmodifiableList()),
            journeyEntry.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toUnmodifiableList()),
            journeyEntry.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            journeyEntry.getCreatedDate().atZone(timeZone),
            journeyEntry.getUpdatedDate().atZone(timeZone),
            Evaluatables.remapClassToClass(journeyEntry.getJourneyName(), new TypeReference<>() {}),
            journeyEntry.getPriority(),
            toStepDataResponses(journeyEntry.getData()),
            journeyEntry.getKey().map(value -> new JourneyKeyResponse(value.getName(), value.getValue())));
    }

    @Override
    public CampaignJourneyEntryConfiguration toConfiguration(CampaignJourneyEntry journeyEntry, ZoneId timeZone,
        Map<Id<CampaignComponent>, String> componentNames) {

        return new CampaignJourneyEntryConfiguration(
            Omissible.of(Id.valueOf(journeyEntry.getId().getValue())),
            journeyEntry.getEnabled(),
            journeyEntry.getTriggers()
                .stream()
                .map(trigger -> toTriggerConfiguration(trigger, timeZone, componentNames))
                .collect(Collectors.toList()),
            journeyEntry.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()),
            Evaluatables.remapClassToClass(journeyEntry.getJourneyName(), new TypeReference<>() {}),
            journeyEntry.getPriority(),
            toStepDataConfiguration(journeyEntry.getData(), componentNames),
            journeyEntry.getKey().map(value -> new JourneyKeyConfiguration(value.getName(), value.getValue())));
    }

    private CampaignControllerTriggerResponse toTriggerResponse(CampaignControllerTrigger trigger, ZoneId timeZone) {
        CampaignControllerTriggerResponseMapper mapper = triggerMapperRepository.getMapper(trigger.getType());
        return mapper.toResponse(trigger, timeZone);
    }

    private CampaignControllerTriggerConfiguration toTriggerConfiguration(CampaignControllerTrigger trigger,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        CampaignControllerTriggerResponseMapper mapper = triggerMapperRepository.getMapper(trigger.getType());
        return mapper.toConfiguration(trigger, timeZone, componentNames);
    }

    private List<StepDataResponse> toStepDataResponses(List<StepData> data) {
        return data.stream()
            .map(value -> toStepDataResponse(value))
            .collect(Collectors.toList());
    }

    private StepDataResponse toStepDataResponse(StepData stepDataValue) {

        return new StepDataResponse(
            stepDataValue.getId().getValue(),
            stepDataValue.getName(),
            stepDataValue.getValue(),
            Evaluatables.remapEnum(stepDataValue.getScope(), new TypeReference<>() {}),
            stepDataValue.isDimension(),
            Evaluatables.remapEnumCollection(stepDataValue.getPersistTypes(), new TypeReference<>() {}),
            stepDataValue.getDefaultValue(),
            Evaluatables.remapEnum(stepDataValue.getKeyType(), new TypeReference<>() {}),
            stepDataValue.getEnabled(),
            stepDataValue.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            stepDataValue.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    private List<StepDataConfiguration> toStepDataConfiguration(List<StepData> data,
        Map<Id<CampaignComponent>, String> componentNames) {
        return data.stream()
            .map(value -> toStepDataConfiguration(value, componentNames))
            .collect(Collectors.toList());
    }

    private StepDataConfiguration toStepDataConfiguration(StepData stepDataValue,
        Map<Id<CampaignComponent>, String> componentNames) {

        List<CampaignComponentReferenceConfiguration> campaignComponentReferences =
            stepDataValue.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference, (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toUnmodifiableList());

        return new StepDataConfiguration(
            Omissible.of(Id.valueOf(stepDataValue.getId().getValue())),
            stepDataValue.getName(),
            stepDataValue.getValue(),
            Evaluatables.remapEnum(stepDataValue.getScope(), new TypeReference<>() {}),
            stepDataValue.isDimension(),
            Evaluatables.remapEnumCollection(stepDataValue.getPersistTypes(), new TypeReference<>() {}),
            stepDataValue.getDefaultValue(),
            Evaluatables.remapEnum(stepDataValue.getKeyType(), new TypeReference<>() {}),
            stepDataValue.getEnabled(),
            campaignComponentReferences);
    }

}
