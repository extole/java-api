package com.extole.client.rest.impl.campaign.built.controller;

import java.time.ZoneId;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.CampaignState;
import com.extole.client.rest.campaign.StepScope;
import com.extole.client.rest.campaign.built.controller.BuiltCampaignControllerResponse;
import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerSelectorType;
import com.extole.client.rest.campaign.controller.SendPolicy;
import com.extole.client.rest.impl.campaign.built.controller.action.BuiltCampaignControllerActionResponseMapper;
import com.extole.client.rest.impl.campaign.built.controller.action.BuiltCampaignControllerActionResponseMapperRepository;
import com.extole.client.rest.impl.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponseMapper;
import com.extole.client.rest.impl.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponseMapperRepository;
import com.extole.client.rest.impl.campaign.step.data.CampaignStepDataRestMapper;
import com.extole.id.Id;
import com.extole.model.entity.campaign.StepType;
import com.extole.model.entity.campaign.built.BuiltCampaignController;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerAction;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTrigger;

@Component
public class BuiltCampaignControllerResponseMapper
    implements BuiltCampaignStepResponseMapper<BuiltCampaignController, BuiltCampaignControllerResponse> {

    private final BuiltCampaignControllerTriggerResponseMapperRepository triggerMapperRepository;
    private final BuiltCampaignControllerActionResponseMapperRepository actionMapperRepository;
    private final CampaignStepDataRestMapper campaignStepDataRestMapper;

    @Autowired
    public BuiltCampaignControllerResponseMapper(
        BuiltCampaignControllerTriggerResponseMapperRepository triggerMapperRepository,
        BuiltCampaignControllerActionResponseMapperRepository actionMapperRepository,
        CampaignStepDataRestMapper campaignStepDataRestMapper) {
        this.triggerMapperRepository = triggerMapperRepository;
        this.actionMapperRepository = actionMapperRepository;
        this.campaignStepDataRestMapper = campaignStepDataRestMapper;
    }

    @Override
    public StepType getStepType() {
        return StepType.CONTROLLER;
    }

    @Override
    public BuiltCampaignControllerResponse toResponse(BuiltCampaignController controller, ZoneId timeZone) {

        return new BuiltCampaignControllerResponse(
            controller.getId().getValue(),
            controller.isEnabled(),
            controller.getTriggers()
                .stream()
                .map(trigger -> toTriggerResponse(trigger, timeZone))
                .collect(Collectors.toList()),
            controller.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            controller.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            controller.getCreatedDate().atZone(timeZone),
            controller.getUpdatedDate().atZone(timeZone),
            controller.getName(),
            StepScope.valueOf(controller.getScope().name()),
            controller.getEnabledOnStates()
                .stream()
                .map(state -> CampaignState.valueOf(state.name()))
                .collect(Collectors.toSet()),
            controller.getSelectors()
                .stream()
                .map(selector -> CampaignControllerSelectorType.valueOf(selector.name()))
                .collect(Collectors.toList()),
            controller.getActions()
                .stream()
                .map(action -> toActionResponse(action, timeZone))
                .collect(Collectors.toList()),
            Lists.newArrayList(controller.getAliases()),
            campaignStepDataRestMapper.toBuiltStepDataResponses(controller.getData()),
            controller.getJourneyNames().stream()
                .map(value -> value.getValue())
                .collect(Collectors.toUnmodifiableSet()),
            SendPolicy.valueOf(controller.getSendPolicy().name()));
    }

    private BuiltCampaignControllerActionResponse toActionResponse(BuiltCampaignControllerAction action,
        ZoneId timeZone) {
        BuiltCampaignControllerActionResponseMapper mapper = actionMapperRepository.getMapper(action.getType());
        return mapper.toResponse(action, timeZone);
    }

    private BuiltCampaignControllerTriggerResponse toTriggerResponse(BuiltCampaignControllerTrigger trigger,
        ZoneId timeZone) {
        BuiltCampaignControllerTriggerResponseMapper mapper = triggerMapperRepository.getMapper(trigger.getType());
        return mapper.toResponse(trigger, timeZone);
    }

}
