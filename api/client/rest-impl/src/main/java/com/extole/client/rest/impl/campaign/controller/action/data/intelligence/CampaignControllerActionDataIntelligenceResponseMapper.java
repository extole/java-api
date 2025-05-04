package com.extole.client.rest.impl.campaign.controller.action.data.intelligence;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionDataIntelligenceConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionDataIntelligence;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionDataIntelligenceResponseMapper implements
    CampaignControllerActionResponseMapper<
        CampaignControllerActionDataIntelligence,
        CampaignControllerActionDataIntelligenceResponse,
        CampaignControllerActionDataIntelligenceConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerActionDataIntelligenceResponseMapper(
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerActionDataIntelligenceResponse toResponse(CampaignControllerActionDataIntelligence action,
        ZoneId timeZone) {
        return new CampaignControllerActionDataIntelligenceResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            Evaluatables.remapEnum(action.getIntelligenceProvider(), new TypeReference<>() {}),
            action.getEventName(),
            action.getProfileRiskUpdateInterval(),
            action.getEnabled(),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerActionDataIntelligenceConfiguration toConfiguration(
        CampaignControllerActionDataIntelligence action,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerActionDataIntelligenceConfiguration(
            Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            Evaluatables.remapEnum(action.getIntelligenceProvider(), new TypeReference<>() {}),
            action.getEventName(),
            action.getProfileRiskUpdateInterval(),
            action.getEnabled(),
            action.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.DATA_INTELLIGENCE;
    }

}
