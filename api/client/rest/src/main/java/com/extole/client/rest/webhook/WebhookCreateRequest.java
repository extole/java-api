package com.extole.client.rest.webhook;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.webhook.WebhookRequest;
import com.extole.api.webhook.WebhookRuntimeContext;
import com.extole.api.webhook.built.WebhookBuildtimeContext;
import com.extole.api.webhook.response.WebhookResponseContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.UrlTemplate;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

@Schema
public class WebhookCreateRequest extends ComponentElementRequest {

    static final String NAME = "name";
    static final String TYPE = "type";
    static final String URL = "url";
    static final String CLIENT_KEY_ID = "client_key_id";
    static final String REQUEST = "request";
    static final String RESPONSE_HANDLER = "response_handler";
    static final String RESPONSE_BODY_HANDLER = "response_body_handler";
    static final String TAGS = "tags";
    static final String ENABLED = "enabled";
    static final String DESCRIPTION = "description";
    static final String DEFAULT_METHOD = "default_method";
    static final String RETRY_INTERVALS = "retry_intervals";

    private final BuildtimeEvaluatable<WebhookBuildtimeContext, String> name;
    private final Omissible<WebhookType> type;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext, UrlTemplate> url;
    private final Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<Id<?>>>> clientKeyId;
    private final Omissible<Set<String>> tags;
    private final Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<WebhookRuntimeContext, WebhookRequest>>> request;
    private final Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<WebhookResponseContext, Optional<String>>>> responseHandler;
    private final Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<WebhookResponseContext, Map<String, Object>>>> responseBodyHandler;
    private final Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<String>>> description;
    private final Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, String>> defaultMethod;
    private final Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<List<Duration>>>> retryIntervals;

    public WebhookCreateRequest(
        @JsonProperty(NAME) BuildtimeEvaluatable<WebhookBuildtimeContext, String> name,
        @JsonProperty(TYPE) Omissible<WebhookType> type,
        @JsonProperty(URL) BuildtimeEvaluatable<WebhookBuildtimeContext, UrlTemplate> url,
        @JsonProperty(CLIENT_KEY_ID) Omissible<
            BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<Id<?>>>> clientKeyId,
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @JsonProperty(REQUEST) Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<WebhookRuntimeContext, WebhookRequest>>> request,
        @JsonProperty(RESPONSE_HANDLER) Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<WebhookResponseContext, Optional<String>>>> responseHandler,
        @JsonProperty(RESPONSE_BODY_HANDLER) Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<WebhookResponseContext, Map<String, Object>>>> responseBodyHandler,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Boolean>> enabled,
        @JsonProperty(DESCRIPTION) Omissible<
            BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<String>>> description,
        @JsonProperty(DEFAULT_METHOD) Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, String>> defaultMethod,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(RETRY_INTERVALS) Omissible<
            BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<List<Duration>>>> retryIntervals) {
        super(componentReferences, componentIds);
        this.name = name;
        this.type = type;
        this.url = url;
        this.clientKeyId = clientKeyId;
        this.tags = tags;
        this.request = request;
        this.responseHandler = responseHandler;
        this.responseBodyHandler = responseBodyHandler;
        this.enabled = enabled;
        this.description = description;
        this.defaultMethod = defaultMethod;
        this.retryIntervals = retryIntervals;
    }

    @JsonProperty(NAME)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(TYPE)
    public Omissible<WebhookType> getType() {
        return type;
    }

    @JsonProperty(URL)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, UrlTemplate> getUrl() {
        return url;
    }

    @JsonProperty(CLIENT_KEY_ID)
    public Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<Id<?>>>> getClientKeyId() {
        return clientKeyId;
    }

    @JsonProperty(REQUEST)
    public
        Omissible<
            BuildtimeEvaluatable<WebhookBuildtimeContext, RuntimeEvaluatable<WebhookRuntimeContext, WebhookRequest>>>
        getRequest() {
        return request;
    }

    @JsonProperty(RESPONSE_HANDLER)
    @Schema(description = "if no webhook response status is provided, we will retry non 2xx webhook requests")
    public
        Omissible<
            BuildtimeEvaluatable<WebhookBuildtimeContext, RuntimeEvaluatable<WebhookResponseContext, Optional<String>>>>
        getResponseHandler() {
        return responseHandler;
    }

    @JsonProperty(RESPONSE_BODY_HANDLER)
    public
        Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<WebhookResponseContext, Map<String, Object>>>>
        getResponseBodyHandler() {
        return responseBodyHandler;
    }

    @JsonProperty(TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(ENABLED)
    public Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Boolean>> isEnabled() {
        return enabled;
    }

    @JsonProperty(DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(DEFAULT_METHOD)
    public Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, String>> getDefaultMethod() {
        return defaultMethod;
    }

    @JsonProperty(RETRY_INTERVALS)
    public Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<List<Duration>>>> getRetryIntervals() {
        return retryIntervals;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private BuildtimeEvaluatable<WebhookBuildtimeContext, String> name;
        private Omissible<WebhookType> type = Omissible.omitted();
        private BuildtimeEvaluatable<WebhookBuildtimeContext, UrlTemplate> url;
        private Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<Id<?>>>> clientKeyId =
            Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<WebhookRuntimeContext, WebhookRequest>>> request =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<WebhookResponseContext, Optional<String>>>> responseHandler =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<WebhookResponseContext, Map<String, Object>>>> responseBodyHandler =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, String>> defaultMethod =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<List<Duration>>>> retryIntervals =
            Omissible.omitted();

        private Builder() {
        }

        public Builder withName(BuildtimeEvaluatable<WebhookBuildtimeContext, String> name) {
            this.name = name;
            return this;
        }

        public Builder withType(WebhookType type) {
            this.type = Omissible.of(type);
            return this;
        }

        public Builder withUrl(BuildtimeEvaluatable<WebhookBuildtimeContext, UrlTemplate> url) {
            this.url = url;
            return this;
        }

        public Builder
            withClientKeyId(BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<Id<?>>> clientKeyId) {
            this.clientKeyId = Omissible.of(clientKeyId);
            return this;
        }

        public Builder withRequest(
            BuildtimeEvaluatable<WebhookBuildtimeContext,
                RuntimeEvaluatable<WebhookRuntimeContext, WebhookRequest>> request) {
            this.request = Omissible.of(request);
            return this;
        }

        public Builder withResponseHandler(
            BuildtimeEvaluatable<WebhookBuildtimeContext,
                RuntimeEvaluatable<WebhookResponseContext, Optional<String>>> responseHandler) {
            this.responseHandler = Omissible.of(responseHandler);
            return this;
        }

        public Builder withResponseBodyHandler(
            BuildtimeEvaluatable<WebhookBuildtimeContext,
                RuntimeEvaluatable<WebhookResponseContext, Map<String, Object>>> responseBodyHandler) {
            this.responseBodyHandler = Omissible.of(responseBodyHandler);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<WebhookBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withDescription(BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder
            withDefaultMethod(BuildtimeEvaluatable<WebhookBuildtimeContext, String> defaultMethod) {
            this.defaultMethod = Omissible.of(defaultMethod);
            return this;
        }

        public Builder
            withRetryIntervals(BuildtimeEvaluatable<WebhookBuildtimeContext, Optional<List<Duration>>> retryIntervals) {
            this.retryIntervals = Omissible.of(retryIntervals);
            return this;
        }

        public WebhookCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new WebhookCreateRequest(name,
                type,
                url,
                clientKeyId,
                tags,
                request,
                responseHandler,
                responseBodyHandler,
                enabled,
                description,
                defaultMethod,
                componentIds,
                componentReferences,
                retryIntervals);
        }

    }

}
