package com.extole.client.rest.webhook.built;

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
import com.extole.api.webhook.response.WebhookResponseContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.webhook.WebhookType;
import com.extole.common.UrlTemplate;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltPartnerWebhookResponse extends BuiltWebhookResponse {

    static final String WEBHOOK_TYPE = "PARTNER";

    private static final String RESPONSE_BODY_HANDLER = "response_body_handler";

    private final RuntimeEvaluatable<WebhookResponseContext, Map<String, Object>> responseBodyHandler;

    @JsonCreator
    public BuiltPartnerWebhookResponse(@JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(URL) UrlTemplate url,
        @JsonProperty(CLIENT_KEY_ID) Optional<Id<?>> clientKeyId,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(REQUEST) RuntimeEvaluatable<WebhookRuntimeContext, WebhookRequest> request,
        @JsonProperty(RESPONSE_HANDLER) RuntimeEvaluatable<WebhookResponseContext, Optional<String>> responseHandler,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(DESCRIPTION) Optional<String> description,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt,
        @JsonProperty(DEFAULT_METHOD) String defaultMethod,
        @JsonProperty(RETRY_INTERVALS) List<Duration> retryIntervals,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(RESPONSE_BODY_HANDLER) RuntimeEvaluatable<WebhookResponseContext,
            Map<String, Object>> responseBodyHandler) {
        super(id, name, WebhookType.PARTNER, url, clientKeyId, tags, request, responseHandler, enabled, description,
            createdAt, updatedAt, defaultMethod, retryIntervals, componentIds, componentReferences);

        this.responseBodyHandler = responseBodyHandler;
    }

    @JsonProperty(RESPONSE_BODY_HANDLER)
    public RuntimeEvaluatable<WebhookResponseContext, Map<String, Object>> getResponseBodyHandler() {
        return responseBodyHandler;
    }

}
