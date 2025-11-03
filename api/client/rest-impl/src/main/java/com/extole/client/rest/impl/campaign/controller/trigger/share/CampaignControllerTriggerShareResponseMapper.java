package com.extole.client.rest.impl.campaign.controller.trigger.share;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerShareConfiguration;
import com.extole.client.rest.campaign.controller.trigger.share.CampaignControllerTriggerShareChannel;
import com.extole.client.rest.campaign.controller.trigger.share.CampaignControllerTriggerShareResponse;
import com.extole.client.rest.campaign.controller.trigger.share.ShareQuality;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTriggerShare;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class CampaignControllerTriggerShareResponseMapper implements
    CampaignControllerTriggerResponseMapper<CampaignControllerTriggerShare, CampaignControllerTriggerShareResponse,
        CampaignControllerTriggerShareConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerTriggerShareResponseMapper(
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerTriggerShareResponse toResponse(CampaignControllerTriggerShare trigger,
        ZoneId timeZone) {
        return new CampaignControllerTriggerShareResponse(trigger.getId().getValue(),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
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
    public CampaignControllerTriggerShareConfiguration toConfiguration(CampaignControllerTriggerShare trigger,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerTriggerShareConfiguration(Omissible.of(Id.valueOf(trigger.getId().getValue())),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            null,
            trigger.getChannels().stream()
                .map(shareType -> com.extole.client.rest.campaign.configuration.CampaignControllerTriggerShareChannel
                    .valueOf(shareType.name()))
                .collect(Collectors.toSet()),
            com.extole.client.rest.campaign.configuration.ShareQuality.valueOf(trigger.getQuality().name()),
            trigger.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.SHARE;
    }

}
