package com.extole.client.rest.impl.campaign.controller.trigger.has.prior.step;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerHasPriorStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTriggerHasPriorStep;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

// TODO Use client time zone or request time zone to format the date response ENG-12531
@Component
public class CampaignControllerTriggerHasPriorStepResponseMapper implements
    CampaignControllerTriggerResponseMapper<CampaignControllerTriggerHasPriorStep,
        CampaignControllerTriggerHasPriorStepResponse, CampaignControllerTriggerHasPriorStepConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerTriggerHasPriorStepResponseMapper(
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerTriggerHasPriorStepResponse toResponse(CampaignControllerTriggerHasPriorStep trigger,
        ZoneId timeZone) {
        return new CampaignControllerTriggerHasPriorStepResponse(
            trigger.getId().getValue(),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getFilterNames(),
            Evaluatables.remapEnum(trigger.getFilterScope(), new TypeReference<>() {}),
            trigger.getFilterPartnerEventIdName(),
            trigger.getFilterPartnerEventIdValue(),
            trigger.getFilterPartnerEventId(),
            trigger.getFilterMinAge(),
            trigger.getFilterMaxAge(),
            trigger.getFilterMinValue(),
            trigger.getFilterMaxValue(),
            Evaluatables.remapEnum(trigger.getFilterQuality(), new TypeReference<>() {}),
            trigger.getFilterExpressions(),
            trigger.getFilterExpression(),
            trigger.getFilterProgramLabel(),
            trigger.getFilterCampaignId(),
            trigger.getFilterProgramLabels(),
            trigger.getFilterCampaignIds(),
            trigger.getFilterMinDate(),
            trigger.getFilterMaxDate(),
            trigger.getSumOfValueMin(),
            trigger.getSumOfValueMax(),
            trigger.getCountMin(),
            trigger.getCountMax(),
            trigger.getCountMatches(),
            trigger.getPersonId(),
            trigger.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            trigger.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            trigger.getHavingAllNames());
    }

    @Override
    public CampaignControllerTriggerHasPriorStepConfiguration toConfiguration(
        CampaignControllerTriggerHasPriorStep trigger,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerTriggerHasPriorStepConfiguration(
            Omissible.of(Id.valueOf(trigger.getId().getValue())),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getFilterNames(),
            Evaluatables.remapEnum(trigger.getFilterScope(), new TypeReference<>() {}),
            trigger.getFilterPartnerEventIdName(),
            trigger.getFilterPartnerEventIdValue(),
            trigger.getFilterPartnerEventId(),
            trigger.getFilterMinAge(),
            trigger.getFilterMaxAge(),
            trigger.getFilterMinValue(),
            trigger.getFilterMaxValue(),
            Evaluatables.remapEnum(trigger.getFilterQuality(), new TypeReference<>() {}),
            trigger.getFilterExpressions(),
            trigger.getFilterExpression(),
            trigger.getFilterProgramLabel(),
            trigger.getFilterCampaignId(),
            trigger.getFilterProgramLabels(),
            trigger.getFilterCampaignIds(),
            trigger.getFilterMinDate(),
            trigger.getFilterMaxDate(),
            trigger.getSumOfValueMin(),
            trigger.getSumOfValueMax(),
            trigger.getCountMin(),
            trigger.getCountMax(),
            trigger.getCountMatches(),
            trigger.getPersonId(),
            trigger.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()),
            trigger.getHavingAllNames());
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.HAS_PRIOR_STEP;
    }

}
