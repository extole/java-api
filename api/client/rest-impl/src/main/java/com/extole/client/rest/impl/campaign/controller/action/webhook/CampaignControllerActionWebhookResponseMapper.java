package com.extole.client.rest.impl.campaign.controller.action.webhook;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionWebhookConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.webhook.CampaignControllerActionWebhookResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.CampaignControllerActionWebhook;

@Component
public class CampaignControllerActionWebhookResponseMapper implements
    CampaignControllerActionResponseMapper<CampaignControllerActionWebhook, CampaignControllerActionWebhookResponse,
        CampaignControllerActionWebhookConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerActionWebhookResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerActionWebhookResponse toResponse(CampaignControllerActionWebhook action, ZoneId timeZone) {
        return new CampaignControllerActionWebhookResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            action.getWebhookId(),
            action.getData());
    }

    @Override
    public CampaignControllerActionWebhookConfiguration toConfiguration(CampaignControllerActionWebhook action,
        ZoneId timeZone,
        Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerActionWebhookConfiguration(
            Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()),
            action.getWebhookId(),
            action.getData());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.WEBHOOK;
    }

}
