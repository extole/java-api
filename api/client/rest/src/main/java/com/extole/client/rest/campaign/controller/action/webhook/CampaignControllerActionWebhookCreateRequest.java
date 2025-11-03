package com.extole.client.rest.campaign.controller.action.webhook;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.webhook.WebhookActionContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionWebhookCreateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_WEBHOOK_ID = "webhook_id";
    private static final String JSON_DATA = "data";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>>> webhookId;
    private final Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<WebhookActionContext, Optional<Object>>>>> data;

    @JsonCreator
    public CampaignControllerActionWebhookCreateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_WEBHOOK_ID) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>>> webhookId,
        @JsonProperty(JSON_DATA) Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<WebhookActionContext, Optional<Object>>>>> data) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.webhookId = webhookId;
        this.data = data;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_WEBHOOK_ID)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>>> getWebhookId() {
        return webhookId;
    }

    @JsonProperty(JSON_DATA)
    public
        Omissible<Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<WebhookActionContext, Optional<Object>>>>>
        getData() {
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<CampaignControllerActionQuality> quality = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>>> webhookId =
            Omissible.omitted();
        private Omissible<Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<WebhookActionContext, Optional<Object>>>>> data =
                    Omissible.omitted();

        private Builder() {
        }

        public Builder withQuality(CampaignControllerActionQuality quality) {
            this.quality = Omissible.of(quality);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withWebhookId(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> webhookId) {
            this.webhookId = Omissible.of(webhookId);
            return this;
        }

        public Builder withData(
            Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<WebhookActionContext, Optional<Object>>>> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public CampaignControllerActionWebhookCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionWebhookCreateRequest(quality,
                enabled,
                componentIds,
                componentReferences,
                webhookId,
                data);
        }

    }

}
