package com.extole.client.rest.impl.campaign.controller.trigger.client.domain;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerClientDomainConfiguration;
import com.extole.client.rest.campaign.controller.trigger.client.domain.CampaignControllerTriggerClientDomainResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTriggerClientDomain;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class CampaignControllerTriggerClientDomainResponseMapper implements
    CampaignControllerTriggerResponseMapper<CampaignControllerTriggerClientDomain,
        CampaignControllerTriggerClientDomainResponse, CampaignControllerTriggerClientDomainConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerTriggerClientDomainResponseMapper(
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerTriggerClientDomainResponse toResponse(
        CampaignControllerTriggerClientDomain trigger,
        ZoneId timeZone) {
        return new CampaignControllerTriggerClientDomainResponse(
            trigger.getId().getValue(),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getClientDomainIds(),
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
    public CampaignControllerTriggerClientDomainConfiguration toConfiguration(
        CampaignControllerTriggerClientDomain trigger, ZoneId timeZone,
        Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerTriggerClientDomainConfiguration(
            Omissible.of(Id.valueOf(trigger.getId().getValue())),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getClientDomainIds(),
            trigger.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.CLIENT_DOMAIN;
    }

}
