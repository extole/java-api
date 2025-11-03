package com.extole.client.rest.impl.campaign.built.controller.trigger;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerShareResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.share.CampaignControllerTriggerShareChannel;
import com.extole.client.rest.campaign.controller.trigger.share.ShareQuality;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerShare;

@Component
public class BuiltCampaignControllerTriggerShareResponseMapper implements
    BuiltCampaignControllerTriggerResponseMapper<BuiltCampaignControllerTriggerShare,
        BuiltCampaignControllerTriggerShareResponse> {

    @Override
    public BuiltCampaignControllerTriggerShareResponse toResponse(BuiltCampaignControllerTriggerShare trigger,
        ZoneId timeZone) {
        return new BuiltCampaignControllerTriggerShareResponse(trigger.getId().getValue(),
            CampaignControllerTriggerPhase.valueOf(trigger.getPhase().name()),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            null,
            trigger.getChannels().stream()
                .map(shareType -> CampaignControllerTriggerShareChannel
                    .valueOf(shareType.name()))
                .collect(Collectors.toSet()),
            ShareQuality.valueOf(trigger.getQuality().name()),
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
        return CampaignControllerTriggerType.SHARE;
    }

}
