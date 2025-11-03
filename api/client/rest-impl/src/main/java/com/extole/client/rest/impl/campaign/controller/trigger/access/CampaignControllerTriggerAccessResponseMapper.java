package com.extole.client.rest.impl.campaign.controller.trigger.access;

import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerAccessConfiguration;
import com.extole.client.rest.campaign.controller.trigger.access.CampaignControllerTriggerAccessResponse;
import com.extole.client.rest.client.Scope;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTriggerAccess;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class CampaignControllerTriggerAccessResponseMapper implements
    CampaignControllerTriggerResponseMapper<CampaignControllerTriggerAccess, CampaignControllerTriggerAccessResponse,
        CampaignControllerTriggerAccessConfiguration> {
    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerTriggerAccessResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerTriggerAccessResponse toResponse(CampaignControllerTriggerAccess trigger,
        ZoneId timeZone) {
        Set<Scope> trustedScopes = trigger.getTrustedScopes().stream()
            .map(trustedScope -> Scope.valueOf(trustedScope.name()))
            .collect(Collectors.toSet());

        return new CampaignControllerTriggerAccessResponse(trigger.getId().getValue(),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trustedScopes,
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
    public CampaignControllerTriggerAccessConfiguration toConfiguration(CampaignControllerTriggerAccess trigger,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        Set<com.extole.client.rest.campaign.configuration.Scope> trustedScopes = trigger.getTrustedScopes().stream()
            .map(trustedScope -> com.extole.client.rest.campaign.configuration.Scope.valueOf(trustedScope.name()))
            .collect(Collectors.toSet());

        return new CampaignControllerTriggerAccessConfiguration(Omissible.of(Id.valueOf(trigger.getId().getValue())),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trustedScopes,
            trigger.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.ACCESS;
    }

}
