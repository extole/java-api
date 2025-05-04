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
import com.extole.client.rest.campaign.configuration.CampaignControllerActionConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFrontendControllerConfiguration;
import com.extole.client.rest.campaign.configuration.StepDataConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.client.rest.campaign.controller.response.CampaignFrontendControllerResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.step.data.StepDataResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapperRepository;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapperRepository;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerAction;
import com.extole.model.entity.campaign.CampaignControllerTrigger;
import com.extole.model.entity.campaign.FrontendController;
import com.extole.model.entity.campaign.StepData;
import com.extole.model.entity.campaign.StepType;

@Component
public class CampaignFrontendControllerResponseMapper implements
    CampaignStepResponseMapper<FrontendController,
        CampaignFrontendControllerResponse, CampaignFrontendControllerConfiguration> {

    private final CampaignControllerActionResponseMapperRepository actionMapperRepository;
    private final CampaignControllerTriggerResponseMapperRepository triggerMapperRepository;
    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignFrontendControllerResponseMapper(
        CampaignControllerActionResponseMapperRepository actionMapperRepository,
        CampaignControllerTriggerResponseMapperRepository triggerMapperRepository,
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.actionMapperRepository = actionMapperRepository;
        this.triggerMapperRepository = triggerMapperRepository;
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public StepType getStepType() {
        return StepType.FRONTEND_CONTROLLER;
    }

    @Override
    public CampaignFrontendControllerResponse toResponse(FrontendController controller, ZoneId timeZone) {

        return new CampaignFrontendControllerResponse(
            controller.getId().getValue(),
            controller.getEnabled(),
            controller.getTriggers()
                .stream()
                .map(trigger -> toTriggerResponse(trigger, timeZone))
                .collect(Collectors.toUnmodifiableList()),
            controller.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toUnmodifiableList()),
            controller.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            controller.getCreatedDate().atZone(timeZone),
            controller.getUpdatedDate().atZone(timeZone),
            controller.getName(),
            Evaluatables.remapEnum(controller.getScope(), new TypeReference<>() {}),
            Evaluatables.remapEnumCollection(controller.getEnabledOnStates(), new TypeReference<>() {}),
            controller.getCategory(),
            controller.getActions()
                .stream()
                .map(action -> toActionResponse(action, timeZone))
                .collect(Collectors.toUnmodifiableList()),
            controller.getAliases(),
            toStepDataResponses(controller.getData()),
            Evaluatables.remapClassToClass(controller.getJourneyNames(), new TypeReference<>() {}),
            Evaluatables.remapEnum(controller.getSendPolicy(), new TypeReference<>() {}));
    }

    @Override
    public CampaignFrontendControllerConfiguration toConfiguration(FrontendController controller, ZoneId timeZone,
        Map<Id<CampaignComponent>, String> componentNames) {

        return new CampaignFrontendControllerConfiguration(
            Omissible.of(Id.valueOf(controller.getId().getValue())),
            controller.getEnabled(),
            controller.getTriggers()
                .stream()
                .map(trigger -> toTriggerConfiguration(trigger, timeZone, componentNames))
                .collect(Collectors.toList()),
            controller.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()),
            controller.getName(),
            Evaluatables.remapEnum(controller.getScope(), new TypeReference<>() {}),
            Evaluatables.remapEnumCollection(controller.getEnabledOnStates(), new TypeReference<>() {}),
            controller.getCategory(),
            controller.getActions()
                .stream()
                .map(action -> toActionConfiguration(action, timeZone, componentNames))
                .collect(Collectors.toList()),
            controller.getAliases(),
            toStepDataConfiguration(controller.getData(), componentNames),
            Evaluatables.remapClassToClass(controller.getJourneyNames(), new TypeReference<>() {}),
            Evaluatables.remapEnum(controller.getSendPolicy(), new TypeReference<>() {}));
    }

    private CampaignControllerActionResponse toActionResponse(CampaignControllerAction action, ZoneId timeZone) {
        CampaignControllerActionResponseMapper mapper = actionMapperRepository.getMapper(action.getType());
        return mapper.toResponse(action, timeZone);
    }

    private CampaignControllerTriggerResponse toTriggerResponse(CampaignControllerTrigger trigger, ZoneId timeZone) {
        CampaignControllerTriggerResponseMapper mapper = triggerMapperRepository.getMapper(trigger.getType());
        return mapper.toResponse(trigger, timeZone);
    }

    private CampaignControllerActionConfiguration toActionConfiguration(CampaignControllerAction action,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        CampaignControllerActionResponseMapper mapper = actionMapperRepository.getMapper(action.getType());
        return mapper.toConfiguration(action, timeZone, componentNames);
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

    private List<StepDataConfiguration> toStepDataConfiguration(List<StepData> data,
        Map<Id<CampaignComponent>, String> componentNames) {
        return data.stream()
            .map(value -> toStepDataConfiguration(value, componentNames))
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
