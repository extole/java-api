package com.extole.client.rest.impl.campaign.controller.trigger.reward.event;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerRewardEventConfiguration;
import com.extole.client.rest.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTriggerRewardEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class CampaignControllerTriggerRewardEventResponseMapper implements
    CampaignControllerTriggerResponseMapper<CampaignControllerTriggerRewardEvent,
        CampaignControllerTriggerRewardEventResponse, CampaignControllerTriggerRewardEventConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerTriggerRewardEventResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerTriggerRewardEventResponse toResponse(CampaignControllerTriggerRewardEvent trigger,
        ZoneId timeZone) {
        return new CampaignControllerTriggerRewardEventResponse(trigger.getId().getValue(),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            Evaluatables.remapClassToClass(trigger.getRewardStates(),
                new TypeReference<>() {}),
            trigger.getEventNames(),
            trigger.getTags(),
            trigger.getTags(),
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
    public CampaignControllerTriggerRewardEventConfiguration toConfiguration(
        CampaignControllerTriggerRewardEvent trigger,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerTriggerRewardEventConfiguration(
            Omissible.of(Id.valueOf(trigger.getId().getValue())),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            Evaluatables.remapClassToClass(trigger.getRewardStates(), new TypeReference<>() {}),
            trigger.getEventNames(),
            trigger.getTags(),
            trigger.getTags(),
            trigger.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.REWARD_EVENT;
    }

}
