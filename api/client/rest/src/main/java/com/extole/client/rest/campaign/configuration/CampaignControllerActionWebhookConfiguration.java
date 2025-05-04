package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.webhook.WebhookActionContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionWebhookConfiguration extends CampaignControllerActionConfiguration {

    private static final String WEBHOOK_ID = "webhook_id";
    private static final String JSON_DATA = "data";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> webhookId;
    private final Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<WebhookActionContext, Optional<Object>>>> data;

    public CampaignControllerActionWebhookConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences,
        @JsonProperty(WEBHOOK_ID) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> webhookId,
        @JsonProperty(JSON_DATA) Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<WebhookActionContext, Optional<Object>>>> data) {
        super(actionId, CampaignControllerActionType.WEBHOOK, quality, enabled, componentReferences);
        this.webhookId = webhookId;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
    }

    @JsonProperty(WEBHOOK_ID)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> getWebhookId() {
        return webhookId;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<WebhookActionContext, Optional<Object>>>> getData() {
        return data;
    }

}
