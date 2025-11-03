package com.extole.client.rest.impl.campaign.built.controller.trigger;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerRewardEventResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.reward.event.RewardState;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerRewardEvent;

@Component
public class BuiltCampaignControllerTriggerRewardEventResponseMapper implements
    BuiltCampaignControllerTriggerResponseMapper<BuiltCampaignControllerTriggerRewardEvent,
        BuiltCampaignControllerTriggerRewardEventResponse> {

    @Override
    public BuiltCampaignControllerTriggerRewardEventResponse toResponse(
        BuiltCampaignControllerTriggerRewardEvent trigger,
        ZoneId timeZone) {
        return new BuiltCampaignControllerTriggerRewardEventResponse(trigger.getId().getValue(),
            CampaignControllerTriggerPhase.valueOf(trigger.getPhase().name()),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getRewardStates().stream()
                .map(item -> RewardState.valueOf(item.name()))
                .collect(Collectors.toSet()),
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
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.REWARD_EVENT;
    }
}
