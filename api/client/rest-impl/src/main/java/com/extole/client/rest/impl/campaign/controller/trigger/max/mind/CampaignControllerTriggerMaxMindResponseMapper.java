package com.extole.client.rest.impl.campaign.controller.trigger.max.mind;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerMaxMindConfiguration;
import com.extole.client.rest.campaign.controller.trigger.max.mind.CampaignControllerTriggerMaxMindResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTriggerMaxMind;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class CampaignControllerTriggerMaxMindResponseMapper implements
    CampaignControllerTriggerResponseMapper<CampaignControllerTriggerMaxMind,
        CampaignControllerTriggerMaxMindResponse,
        CampaignControllerTriggerMaxMindConfiguration> {
    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerTriggerMaxMindResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerTriggerMaxMindResponse toResponse(CampaignControllerTriggerMaxMind trigger,
        ZoneId timeZone) {
        return new CampaignControllerTriggerMaxMindResponse(trigger.getId().getValue(),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            Evaluatables.remapEnum(trigger.getDefaultQualityScore(), new TypeReference<>() {}),
            trigger.getRiskThreshold(),
            trigger.getIpThreshold(),
            trigger.allowHighRiskEmail(),
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
    public CampaignControllerTriggerMaxMindConfiguration toConfiguration(CampaignControllerTriggerMaxMind trigger,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerTriggerMaxMindConfiguration(Omissible.of(Id.valueOf(trigger.getId().getValue())),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            Evaluatables.remapEnum(trigger.getDefaultQualityScore(), new TypeReference<>() {}),
            trigger.getRiskThreshold(),
            trigger.getIpThreshold(),
            trigger.allowHighRiskEmail(),
            trigger.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.MAXMIND;
    }

}
