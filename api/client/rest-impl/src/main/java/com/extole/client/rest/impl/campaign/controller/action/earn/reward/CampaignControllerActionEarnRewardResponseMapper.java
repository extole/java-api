package com.extole.client.rest.impl.campaign.controller.action.earn.reward;

import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.reward.RewardActionContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionEarnRewardConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionEarnReward;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionEarnRewardResponseMapper implements
    CampaignControllerActionResponseMapper<CampaignControllerActionEarnReward,
        CampaignControllerActionEarnRewardResponse, CampaignControllerActionEarnRewardConfiguration> {

    private static final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<RewardActionContext, Optional<Object>>> DEFAULT_VALUE_OF_VALUE_OF_EVENT_BEING_REWARDED;

    static {
        try {
            DEFAULT_VALUE_OF_VALUE_OF_EVENT_BEING_REWARDED = ObjectMapperProvider.getConfiguredInstance()
                .readValue("\"javascript@runtime:context.getCauseEvent().getData()[\\\"value\\\"]\"",
                    new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerActionEarnRewardResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerActionEarnRewardResponse
        toResponse(CampaignControllerActionEarnReward action, ZoneId timeZone) {
        return new CampaignControllerActionEarnRewardResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getRewardName(),
            action.getRewardSupplierId(),
            action.getTags(),
            action.getData().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
            action.getData().getOrDefault(Provided.of("earned_event_value"),
                DEFAULT_VALUE_OF_VALUE_OF_EVENT_BEING_REWARDED),
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
            action.getEventTime(),
            action.getRewardActionId(),
            action.getExtraData());
    }

    @Override
    public CampaignControllerActionEarnRewardConfiguration
        toConfiguration(CampaignControllerActionEarnReward action, ZoneId timeZone,
            Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerActionEarnRewardConfiguration(
            Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            action.getRewardName(),
            action.getRewardSupplierId(),
            action.getTags(),
            action.getData().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
            action.getData().getOrDefault(Provided.of("earned_event_value"),
                DEFAULT_VALUE_OF_VALUE_OF_EVENT_BEING_REWARDED),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()),
            action.getEventTime(),
            action.getRewardActionId(),
            action.getExtraData());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.EARN_REWARD;
    }

}
