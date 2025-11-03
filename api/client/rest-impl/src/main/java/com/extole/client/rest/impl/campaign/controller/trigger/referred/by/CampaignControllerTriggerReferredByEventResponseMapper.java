package com.extole.client.rest.impl.campaign.controller.trigger.referred.by;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerReferredByEventConfiguration;
import com.extole.client.rest.campaign.controller.trigger.referred.by.CampaignControllerTriggerReferralOriginator;
import com.extole.client.rest.campaign.controller.trigger.referred.by.CampaignControllerTriggerReferredByEventResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTriggerReferredByEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class CampaignControllerTriggerReferredByEventResponseMapper implements
    CampaignControllerTriggerResponseMapper<CampaignControllerTriggerReferredByEvent,
        CampaignControllerTriggerReferredByEventResponse, CampaignControllerTriggerReferredByEventConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerTriggerReferredByEventResponseMapper(
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerTriggerReferredByEventResponse
        toResponse(CampaignControllerTriggerReferredByEvent trigger, ZoneId timeZone) {
        return new CampaignControllerTriggerReferredByEventResponse(trigger.getId().getValue(),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getReferralOriginator().isPresent()
                ? CampaignControllerTriggerReferralOriginator.valueOf(trigger.getReferralOriginator().get().name())
                : null,
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
    public CampaignControllerTriggerReferredByEventConfiguration
        toConfiguration(CampaignControllerTriggerReferredByEvent trigger, ZoneId timeZone,
            Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerTriggerReferredByEventConfiguration(
            Omissible.of(Id.valueOf(trigger.getId().getValue())),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getReferralOriginator().isPresent()
                ? com.extole.client.rest.campaign.configuration.CampaignControllerTriggerReferralOriginator
                    .valueOf(trigger.getReferralOriginator().get().name())
                : null,
            trigger.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.REFERRED_BY_EVENT;
    }

}
