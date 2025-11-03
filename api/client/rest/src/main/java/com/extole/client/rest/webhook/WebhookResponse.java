package com.extole.client.rest.webhook;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableSet;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.webhook.WebhookRequest;
import com.extole.api.webhook.WebhookRuntimeContext;
import com.extole.api.webhook.built.WebhookBuildtimeContext;
import com.extole.api.webhook.response.WebhookResponseContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.UrlTemplate;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = WebhookResponse.TYPE)
@JsonSubTypes({
    @Type(value = GenericWebhookResponse.class,
        name = GenericWebhookResponse.WEBHOOK_TYPE),
    @Type(value = RewardWebhookResponse.class,
        name = RewardWebhookResponse.WEBHOOK_TYPE),
    @Type(value = ClientWebhookResponse.class,
        name = ClientWebhookResponse.WEBHOOK_TYPE),
    @Type(value = PartnerWebhookResponse.class,
        name = PartnerWebhookResponse.WEBHOOK_TYPE),
})
@Schema(discriminatorProperty = "type", discriminatorMapping = {
    @DiscriminatorMapping(value = GenericWebhookResponse.WEBHOOK_TYPE,
        schema = GenericWebhookResponse.class),
    @DiscriminatorMapping(value = RewardWebhookResponse.WEBHOOK_TYPE,
        schema = RewardWebhookResponse.class),
    @DiscriminatorMapping(value = ClientWebhookResponse.WEBHOOK_TYPE,
        schema = ClientWebhookResponse.class),
    @DiscriminatorMapping(value = PartnerWebhookResponse.WEBHOOK_TYPE,
        schema = PartnerWebhookResponse.class),
})
public abstract class WebhookResponse extends ComponentElementResponse {

    static final String ID = "id";
    static final String NAME = "name";
    static final String TYPE = "type";
    static final String URL = "url";
    static final String CLIENT_KEY_ID = "client_key_id";
    static final String RESPONSE_HANDLER = "response_handler";
    static final String REQUEST = "request";
    static final String TAGS = "tags";
    static final String ENABLED = "enabled";
    static final String DESCRIPTION = "description";
    static final String CREATED_AT = "created_at";
    static final String UPDATED_AT = "updated_at";
    static final String DEFAULT_METHOD = "default_method";
    static final String RETRY_INTERVALS = "retry_intervals";

    private final String id;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext, String> name;
    private final WebhookType type;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext, UrlTemplate> url;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<Id<?>>> clientKeyId;
    private final Set<String> tags;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<WebhookRuntimeContext, WebhookRequest>> request;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<WebhookResponseContext, Optional<String>>> responseHandler;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext, Boolean> enabled;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<String>> description;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext, String> defaultMethod;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<List<Duration>>> retryIntervals;

    public WebhookResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) BuildtimeEvaluatable<WebhookBuildtimeContext, String> name,
        @JsonProperty(TYPE) WebhookType type,
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
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.clientKeyId = clientKeyId;
        this.tags = tags != null ? ImmutableSet.copyOf(tags) : ImmutableSet.of();
        this.request = request;
        this.responseHandler = responseHandler;
        this.enabled = enabled;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.defaultMethod = defaultMethod;
        this.retryIntervals = retryIntervals;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(TYPE)
    public WebhookType getType() {
        return type;
    }

    @JsonProperty(URL)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, UrlTemplate> getUrl() {
        return url;
    }

    @JsonProperty(CLIENT_KEY_ID)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<Id<?>>> getClientKeyId() {
        return clientKeyId;
    }

    @JsonProperty(REQUEST)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, RuntimeEvaluatable<WebhookRuntimeContext, WebhookRequest>>
        getRequest() {
        return request;
    }

    @JsonProperty(RESPONSE_HANDLER)
    @Schema(description = "if no webhook response status is provided, we will retry non 2xx webhook requests")
    public BuildtimeEvaluatable<WebhookBuildtimeContext, RuntimeEvaluatable<WebhookResponseContext, Optional<String>>>
        getResponseHandler() {
        return responseHandler;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(ENABLED)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, Boolean> getEnabled() {
        return enabled;
    }

    @JsonProperty(DESCRIPTION)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(CREATED_AT)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonProperty(UPDATED_AT)
    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty(DEFAULT_METHOD)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, String> getDefaultMethod() {
        return defaultMethod;
    }

    @JsonProperty(RETRY_INTERVALS)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<List<Duration>>> getRetryIntervals() {
        return retryIntervals;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
