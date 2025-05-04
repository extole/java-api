package com.extole.client.rest.impl.campaign.built.controller.trigger;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerMaxMindResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.max.mind.QualityScore;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerMaxMind;

@Component
public class BuiltCampaignControllerTriggerMaxMindResponseMapper implements
    BuiltCampaignControllerTriggerResponseMapper<BuiltCampaignControllerTriggerMaxMind,
        BuiltCampaignControllerTriggerMaxMindResponse> {

    @Override
    public BuiltCampaignControllerTriggerMaxMindResponse toResponse(BuiltCampaignControllerTriggerMaxMind trigger,
        ZoneId timeZone) {
        return new BuiltCampaignControllerTriggerMaxMindResponse(
            trigger.getId().getValue(),
            CampaignControllerTriggerPhase.valueOf(trigger.getPhase().name()),
            trigger.getName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            QualityScore.valueOf(trigger.getDefaultQualityScore().name()),
            Long.valueOf(trigger.getRiskThreshold()),
            Long.valueOf(trigger.getIpThreshold()),
            Boolean.valueOf(trigger.allowHighRiskEmail()),
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
        return CampaignControllerTriggerType.MAXMIND;
    }

}
