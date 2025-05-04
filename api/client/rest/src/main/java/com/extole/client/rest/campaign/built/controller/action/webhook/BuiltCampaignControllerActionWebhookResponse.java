package com.extole.client.rest.campaign.built.controller.action.webhook;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.step.action.webhook.WebhookActionContext;
import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignControllerActionWebhookResponse extends BuiltCampaignControllerActionResponse {

    private static final String WEBHOOK_ID = "webhook_id";
    private static final String JSON_DATA = "data";

    private final Optional<String> webhookId;
    private final Map<String, RuntimeEvaluatable<WebhookActionContext, Optional<Object>>> data;

    public BuiltCampaignControllerActionWebhookResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(WEBHOOK_ID) Optional<String> webhookId,
        @JsonProperty(JSON_DATA) Map<String, RuntimeEvaluatable<WebhookActionContext, Optional<Object>>> data) {
        super(actionId, CampaignControllerActionType.WEBHOOK, quality, enabled, componentIds, componentReferences);
        this.webhookId = webhookId;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
    }

    @JsonProperty(WEBHOOK_ID)
    public Optional<String> getWebhookId() {
        return webhookId;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, RuntimeEvaluatable<WebhookActionContext, Optional<Object>>> getData() {
        return data;
    }

}
