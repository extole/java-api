package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

import com.extole.api.step.action.reward.RewardActionContext;
import com.extole.client.rest.campaign.built.controller.action.earn.reward.BuiltCampaignControllerActionEarnRewardResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionEarnReward;

@Component
public class BuiltCampaignControllerActionEarnRewardResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<BuiltCampaignControllerActionEarnReward,
        BuiltCampaignControllerActionEarnRewardResponse> {

    private static final RuntimeEvaluatable<RewardActionContext,
        Optional<Object>> DEFAULT_VALUE_OF_VALUE_OF_EVENT_BEING_REWARDED;

    static {
        try {
            DEFAULT_VALUE_OF_VALUE_OF_EVENT_BEING_REWARDED = ObjectMapperProvider.getConfiguredInstance()
                .readValue("\"javascript@runtime:context.getCauseEvent().getData()[\\\"value\\\"]\"",
                    new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BuiltCampaignControllerActionEarnRewardResponse
        toResponse(BuiltCampaignControllerActionEarnReward action, ZoneId timeZone) {
        return new BuiltCampaignControllerActionEarnRewardResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getRewardName(),
            action.getRewardSupplierId().map(id -> id.getValue()),
            action.getTags(),
            action.getData().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
            action.getData().getOrDefault("earned_event_value", DEFAULT_VALUE_OF_VALUE_OF_EVENT_BEING_REWARDED),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            action.getRewardActionId(),
            action.getExtraData());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.EARN_REWARD;
    }

}
