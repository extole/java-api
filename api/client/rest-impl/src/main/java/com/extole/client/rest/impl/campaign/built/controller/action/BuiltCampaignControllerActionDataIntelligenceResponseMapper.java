package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.data.intelligence.BuiltCampaignControllerActionDataIntelligenceResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.data.intelligence.DataIntelligenceProviderType;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionDataIntelligence;

@Component
public class BuiltCampaignControllerActionDataIntelligenceResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<BuiltCampaignControllerActionDataIntelligence,
        BuiltCampaignControllerActionDataIntelligenceResponse> {

    @Override
    public BuiltCampaignControllerActionDataIntelligenceResponse toResponse(
        BuiltCampaignControllerActionDataIntelligence action,
        ZoneId timeZone) {
        return new BuiltCampaignControllerActionDataIntelligenceResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            DataIntelligenceProviderType.valueOf(action.getIntelligenceProvider().name()),
            action.getEventName(),
            action.getProfileRiskUpdateInterval(),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.DATA_INTELLIGENCE;
    }

}
