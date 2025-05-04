package com.extole.client.rest.webhook.built;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.webhook.WebhookRequest;
import com.extole.api.webhook.WebhookRuntimeContext;
import com.extole.api.webhook.response.WebhookResponseContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.webhook.WebhookType;
import com.extole.common.UrlTemplate;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = BuiltWebhookResponse.TYPE)
@JsonSubTypes({
    @Type(value = BuiltGenericWebhookResponse.class,
        name = BuiltGenericWebhookResponse.WEBHOOK_TYPE),
    @Type(value = BuiltRewardWebhookResponse.class,
        name = BuiltRewardWebhookResponse.WEBHOOK_TYPE),
    @Type(value = BuiltClientWebhookResponse.class,
        name = BuiltClientWebhookResponse.WEBHOOK_TYPE),
    @Type(value = BuiltPartnerWebhookResponse.class,
        name = BuiltPartnerWebhookResponse.WEBHOOK_TYPE),
})
@Schema(discriminatorProperty = "type", discriminatorMapping = {
    @DiscriminatorMapping(value = BuiltGenericWebhookResponse.WEBHOOK_TYPE,
        schema = BuiltGenericWebhookResponse.class),
    @DiscriminatorMapping(value = BuiltRewardWebhookResponse.WEBHOOK_TYPE,
        schema = BuiltRewardWebhookResponse.class),
    @DiscriminatorMapping(value = BuiltClientWebhookResponse.WEBHOOK_TYPE,
        schema = BuiltClientWebhookResponse.class),
    @DiscriminatorMapping(value = BuiltPartnerWebhookResponse.WEBHOOK_TYPE,
        schema = BuiltPartnerWebhookResponse.class),
})
public abstract class BuiltWebhookResponse extends ComponentElementResponse {

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
    private final String name;
    private final WebhookType type;
    private final UrlTemplate url;
    private final Optional<Id<?>> clientKeyId;
    private final Set<String> tags;
    private final RuntimeEvaluatable<WebhookRuntimeContext, WebhookRequest> request;
    private final RuntimeEvaluatable<WebhookResponseContext, Optional<String>> responseHandler;
    private final Boolean enabled;
    private final Optional<String> description;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final String defaultMethod;
    private final List<Duration> retryIntervals;

    public BuiltWebhookResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(TYPE) WebhookType type,
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
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.clientKeyId = clientKeyId;
        this.tags = tags;
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
    public String getName() {
        return name;
    }

    @JsonProperty(TYPE)
    public WebhookType getType() {
        return type;
    }

    @JsonProperty(URL)
    public UrlTemplate getUrl() {
        return url;
    }

    @JsonProperty(CLIENT_KEY_ID)
    public Optional<Id<?>> getClientKeyId() {
        return clientKeyId;
    }

    @JsonProperty(REQUEST)
    public RuntimeEvaluatable<WebhookRuntimeContext, WebhookRequest> getRequest() {
        return request;
    }

    @JsonProperty(RESPONSE_HANDLER)
    @Schema(description = "if no webhook response status is provided, we will retry non 2xx webhook requests")
    public RuntimeEvaluatable<WebhookResponseContext, Optional<String>> getResponseHandler() {
        return responseHandler;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(ENABLED)
    public Boolean isEnabled() {
        return enabled;
    }

    @JsonProperty(DESCRIPTION)
    public Optional<String> getDescription() {
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
    public String getDefaultMethod() {
        return defaultMethod;
    }

    @JsonProperty(RETRY_INTERVALS)
    public List<Duration> getRetryIntervals() {
        return retryIntervals;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
