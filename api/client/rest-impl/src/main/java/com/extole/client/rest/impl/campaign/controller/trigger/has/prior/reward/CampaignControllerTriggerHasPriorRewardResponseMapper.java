package com.extole.client.rest.impl.campaign.controller.trigger.has.prior.reward;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerHasPriorRewardConfiguration;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTriggerHasPriorReward;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class CampaignControllerTriggerHasPriorRewardResponseMapper implements
    CampaignControllerTriggerResponseMapper<CampaignControllerTriggerHasPriorReward,
        CampaignControllerTriggerHasPriorRewardResponse,
        CampaignControllerTriggerHasPriorRewardConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerTriggerHasPriorRewardResponseMapper(
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerTriggerHasPriorRewardResponse toResponse(CampaignControllerTriggerHasPriorReward trigger,
        ZoneId timeZone) {
        return new CampaignControllerTriggerHasPriorRewardResponse(trigger.getId().getValue(),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getFilterNames(),
            Evaluatables.remapEnum(trigger.getFilterScope(), new TypeReference<>() {}),
            trigger.getFilterTags(),
            trigger.getFilterMinAge(),
            trigger.getFilterMaxAge(),
            trigger.getFilterMinDate(),
            trigger.getFilterMaxDate(),
            trigger.getFilterRewardSupplierIds(),
            Evaluatables.remapEnumCollection(trigger.getFilterFaceValueTypes(), new TypeReference<>() {}),
            Evaluatables.remapEnumCollection(trigger.getFilterStates(), new TypeReference<>() {}),
            trigger.getFilterExpressions(),
            trigger.getFilterExpression(),
            trigger.getSumOfFaceValueMax(),
            trigger.getSumOfFaceValueMin(),
            trigger.getCountMax(),
            trigger.getCountMin(),
            trigger.getCountMatches(),
            trigger.getTaxYearStart(),
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
    public CampaignControllerTriggerHasPriorRewardConfiguration toConfiguration(
        CampaignControllerTriggerHasPriorReward trigger, ZoneId timeZone,
        Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerTriggerHasPriorRewardConfiguration(
            Omissible.of(Id.valueOf(trigger.getId().getValue())),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getFilterNames(),
            Evaluatables.remapEnum(trigger.getFilterScope(), new TypeReference<>() {}),
            trigger.getFilterTags(),
            trigger.getFilterMinAge(),
            trigger.getFilterMaxAge(),
            trigger.getFilterMinDate(),
            trigger.getFilterMaxDate(),
            trigger.getFilterRewardSupplierIds(),
            Evaluatables.remapEnumCollection(trigger.getFilterFaceValueTypes(), new TypeReference<>() {}),
            Evaluatables.remapEnumCollection(trigger.getFilterStates(), new TypeReference<>() {}),
            trigger.getFilterExpressions(),
            trigger.getFilterExpression(),
            trigger.getSumOfFaceValueMax(),
            trigger.getSumOfFaceValueMin(),
            trigger.getCountMax(),
            trigger.getCountMin(),
            trigger.getCountMatches(),
            trigger.getTaxYearStart(),
            trigger.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.HAS_PRIOR_REWARD;
    }

}
