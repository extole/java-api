package com.extole.client.rest.impl.campaign.built.controller.trigger;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerHasPriorStepResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.StepFilterScope;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.StepQuality;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerHasPriorStep;

// TODO Use client time zone or request time zone to format the date response ENG-12531
@Component
public class BuiltCampaignControllerTriggerHasPriorStepResponseMapper implements
    BuiltCampaignControllerTriggerResponseMapper<BuiltCampaignControllerTriggerHasPriorStep,
        BuiltCampaignControllerTriggerHasPriorStepResponse> {

    @Override
    public BuiltCampaignControllerTriggerHasPriorStepResponse toResponse(
        BuiltCampaignControllerTriggerHasPriorStep trigger,
        ZoneId timeZone) {
        return new BuiltCampaignControllerTriggerHasPriorStepResponse(trigger.getId().getValue(),
            CampaignControllerTriggerPhase.valueOf(trigger.getPhase().name()),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getFilterNames(),
            StepFilterScope.valueOf(trigger.getFilterScope().name()),
            trigger.getFilterPartnerEventIdName(),
            trigger.getFilterPartnerEventIdValue(),
            trigger.getFilterPartnerEventId(),
            trigger.getFilterMinAge(),
            trigger.getFilterMaxAge(),
            trigger.getFilterMinValue(),
            trigger.getFilterMaxValue(),
            StepQuality.valueOf(trigger.getFilterQuality().name()),
            trigger.getFilterExpressions(),
            trigger.getFilterExpression(),
            trigger.getFilterProgramLabel(),
            trigger.getFilterCampaignId(),
            trigger.getFilterProgramLabels(),
            trigger.getFilterCampaignIds(),
            trigger.getFilterMinDate().map(zonedDateTime -> zonedDateTime.withZoneSameInstant(timeZone)),
            trigger.getFilterMaxDate().map(zonedDateTime -> zonedDateTime.withZoneSameInstant(timeZone)),
            trigger.getSumOfValueMin(),
            trigger.getSumOfValueMax(),
            trigger.getCountMin(),
            trigger.getCountMax(),
            trigger.getCountMatches(),
            trigger.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            trigger.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            trigger.getPersonId(),
            trigger.getHavingAllNames());
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.HAS_PRIOR_STEP;
    }

}
