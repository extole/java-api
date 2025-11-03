package com.extole.client.rest.impl.campaign.built.controller.trigger;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerLegacyQualityResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.legacy.quality.CampaignControllerTriggerActionType;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerLegacyQuality;

@Component
public class BuiltCampaignControllerTriggerLegacyQualityResponseMapper implements
    BuiltCampaignControllerTriggerResponseMapper<BuiltCampaignControllerTriggerLegacyQuality,
        BuiltCampaignControllerTriggerLegacyQualityResponse> {

    @Override
    public BuiltCampaignControllerTriggerLegacyQualityResponse
        toResponse(BuiltCampaignControllerTriggerLegacyQuality trigger, ZoneId timeZone) {
        return new BuiltCampaignControllerTriggerLegacyQualityResponse(trigger.getId().getValue(),
            CampaignControllerTriggerPhase.valueOf(trigger.getPhase().name()),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            CampaignControllerTriggerActionType.valueOf(trigger.getActionType().name()),
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
        return CampaignControllerTriggerType.LEGACY_QUALITY;
    }

}
