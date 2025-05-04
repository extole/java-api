package com.extole.client.rest.webhook;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.webhook.WebhookRequest;
import com.extole.api.webhook.WebhookRuntimeContext;
import com.extole.api.webhook.built.WebhookBuildtimeContext;
import com.extole.api.webhook.response.WebhookResponseContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.UrlTemplate;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class PartnerWebhookResponse extends WebhookResponse {

    static final String WEBHOOK_TYPE = "PARTNER";

    private static final String RESPONSE_BODY_HANDLER = "response_body_handler";

    private final BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<WebhookResponseContext, Map<String, Object>>> responseBodyHandler;

    @JsonCreator
    public PartnerWebhookResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) BuildtimeEvaluatable<WebhookBuildtimeContext, String> name,
        @JsonProperty(URL) BuildtimeEvaluatable<WebhookBuildtimeContext, UrlTemplate> url,
        @JsonProperty(CLIENT_KEY_ID) BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<Id<?>>> clientKeyId,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(REQUEST) BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<WebhookRuntimeContext, WebhookRequest>> request,
        @JsonProperty(RESPONSE_HANDLER) BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<WebhookResponseContext, Optional<String>>> responseHandler,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<WebhookBuildtimeContext, Boolean> enabled,
        @JsonProperty(DESCRIPTION) BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<String>> description,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt,
        @JsonProperty(DEFAULT_METHOD) BuildtimeEvaluatable<WebhookBuildtimeContext, String> defaultMethod,
        @JsonProperty(RETRY_INTERVALS) BuildtimeEvaluatable<WebhookBuildtimeContext,
            Optional<List<Duration>>> retryIntervals,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(RESPONSE_BODY_HANDLER) BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<WebhookResponseContext, Map<String, Object>>> responseBodyHandler) {
        super(id, name, WebhookType.PARTNER, url, clientKeyId, tags, request, responseHandler, enabled, description,
            createdAt, updatedAt, defaultMethod, retryIntervals, componentIds, componentReferences);

        this.responseBodyHandler = responseBodyHandler;
    }

    @JsonProperty(RESPONSE_BODY_HANDLER)
    public BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<WebhookResponseContext, Map<String, Object>>> getResponseBodyHandler() {
        return responseBodyHandler;
    }

}
