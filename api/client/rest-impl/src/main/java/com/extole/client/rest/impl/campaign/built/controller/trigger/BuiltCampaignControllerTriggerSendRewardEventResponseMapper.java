package com.extole.client.rest.impl.campaign.built.controller.trigger;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerSendRewardEventResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.send.reward.event.RewardState;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerSendRewardEvent;

@Component
public class BuiltCampaignControllerTriggerSendRewardEventResponseMapper implements
    BuiltCampaignControllerTriggerResponseMapper<BuiltCampaignControllerTriggerSendRewardEvent,
        BuiltCampaignControllerTriggerSendRewardEventResponse> {

    @Override
    public BuiltCampaignControllerTriggerSendRewardEventResponse toResponse(
        BuiltCampaignControllerTriggerSendRewardEvent trigger,
        ZoneId timeZone) {
        return new BuiltCampaignControllerTriggerSendRewardEventResponse(
            trigger.getId().getValue(),
            CampaignControllerTriggerPhase.valueOf(trigger.getPhase().name()),
            trigger.getName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getRewardStates().stream()
                .map(item -> RewardState.valueOf(item.name()))
                .collect(Collectors.toUnmodifiableSet()),
            trigger.getRewardNames(),
            trigger.getTags(),
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
        return CampaignControllerTriggerType.SEND_REWARD_EVENT;
    }

}
