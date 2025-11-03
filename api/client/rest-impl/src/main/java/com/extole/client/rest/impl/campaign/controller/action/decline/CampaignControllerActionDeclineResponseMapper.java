package com.extole.client.rest.impl.campaign.controller.action.decline;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionDeclineConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.decline.CampaignControllerActionDeclineResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionDecline;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionDeclineResponseMapper implements
    CampaignControllerActionResponseMapper<CampaignControllerActionDecline, CampaignControllerActionDeclineResponse,
        CampaignControllerActionDeclineConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerActionDeclineResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerActionDeclineResponse toResponse(CampaignControllerActionDecline action, ZoneId timeZone) {
        return new CampaignControllerActionDeclineResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getLegacyActionId(),
            action.getPartnerEventId(),
            action.getEventType(),
            action.getNote(),
            action.getCauseType(),
            action.getPollingId(),
            action.getPollingName(),
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
    public CampaignControllerActionDeclineConfiguration toConfiguration(CampaignControllerActionDecline action,
        ZoneId timeZone,
        Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerActionDeclineConfiguration(
            Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            action.getLegacyActionId(),
            action.getPartnerEventId(),
            action.getEventType(),
            action.getNote(),
            action.getCauseType(),
            action.getPollingId(),
            action.getPollingName(),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.DECLINE;
    }

}
