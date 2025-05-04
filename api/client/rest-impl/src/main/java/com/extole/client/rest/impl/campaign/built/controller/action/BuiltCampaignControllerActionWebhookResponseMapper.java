package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.webhook.BuiltCampaignControllerActionWebhookResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionWebhook;

@Component
public class BuiltCampaignControllerActionWebhookResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<
        BuiltCampaignControllerActionWebhook,
        BuiltCampaignControllerActionWebhookResponse> {

    @Override
    public BuiltCampaignControllerActionWebhookResponse toResponse(BuiltCampaignControllerActionWebhook action,
        ZoneId timeZone) {
        return new BuiltCampaignControllerActionWebhookResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getEnabled(),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            action.getWebhookId().map(id -> id.getValue()),
            action.getData());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.WEBHOOK;
    }

}
