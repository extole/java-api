package com.extole.webhook.dispatcher.rest.dispatch.result;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;

public class WebhookDispatchResultResponse {

    private static final String EVENT_ID = "event_id";
    private static final String EVENT_TIME = "event_time";
    private static final String WEBHOOK_EVENT_ID = "webhook_event_id";
    private static final String CAUSE_EVENT_ID = "cause_event_id";
    private static final String ROOT_EVENT_ID = "root_event_id";
    private static final String WEBHOOK_ID = "webhook_id";
    private static final String URL = "url";
    private static final String REQUEST_BODY = "request_body";
    private static final String REQUEST_HEADERS = "request_headers";
    private static final String RESPONSE_STATUS_CODE = "response_status_code";
    private static final String RESPONSE_BODY = "response_body";
    private static final String RESPONSE_HEADERS = "response_headers";
    private static final String ATTEMPT_COUNT = "attempt_count";
    private static final String METHOD = "method";
    private static final String LOG_MESSAGES = "log_messages";

    private static final String TAGS = "tags";
    private static final String RESPONSE = "response";

    private final String eventId;
    private final ZonedDateTime eventTime;
    private final String webhookEventId;
    private final String causeEventId;
    private final String rootEventId;
    private final String webhookId;
    private final Optional<String> url;
    private final Optional<String> requestBody;
    private final Map<String, List<String>> requestHeaders;
    private final Optional<Integer> responseStatusCode;
    private final Optional<String> responseBody;
    private final Map<String, List<String>> responseHeaders;
    private final int attemptCount;
    private final Optional<String> method;
    private final List<String> logMessages;
    private final List<String> tags;
    private final Map<String, Object> response;

    public WebhookDispatchResultResponse(
        @JsonProperty(EVENT_ID) String eventId,
        @JsonProperty(EVENT_TIME) ZonedDateTime eventTime,
        @JsonProperty(WEBHOOK_EVENT_ID) String webhookEventId,
        @JsonProperty(CAUSE_EVENT_ID) String causeEventId,
        @JsonProperty(ROOT_EVENT_ID) String rootEventId,
        @JsonProperty(WEBHOOK_ID) String webhookId,
        @JsonProperty(URL) Optional<String> url,
        @JsonProperty(REQUEST_BODY) Optional<String> requestBody,
        @JsonProperty(REQUEST_HEADERS) Map<String, List<String>> requestHeaders,
        @JsonProperty(RESPONSE_STATUS_CODE) Optional<Integer> responseStatusCode,
        @JsonProperty(RESPONSE_BODY) Optional<String> responseBody,
        @JsonProperty(RESPONSE_HEADERS) Map<String, List<String>> responseHeaders,
        @JsonProperty(ATTEMPT_COUNT) int attemptCount,
        @JsonProperty(METHOD) Optional<String> method,
        @JsonProperty(LOG_MESSAGES) List<String> logMessages,
        @JsonProperty(TAGS) List<String> tags,
        @JsonProperty(RESPONSE) Map<String, Object> response) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.webhookEventId = webhookEventId;
        this.causeEventId = causeEventId;
        this.rootEventId = rootEventId;
        this.webhookId = webhookId;
        this.url = url;
        this.requestBody = requestBody;
        this.requestHeaders = requestHeaders != null ? ImmutableMap.copyOf(requestHeaders) : ImmutableMap.of();
        this.responseStatusCode = responseStatusCode;
        this.responseBody = responseBody;
        this.responseHeaders = responseHeaders != null ? ImmutableMap.copyOf(responseHeaders) : ImmutableMap.of();
        this.attemptCount = attemptCount;
        this.method = method;
        this.logMessages = logMessages != null ? ImmutableList.copyOf(logMessages) : ImmutableList.of();
        this.tags = tags != null ? ImmutableList.copyOf(tags) : ImmutableList.of();
        this.response = response != null ? ImmutableMap.copyOf(response) : ImmutableMap.of();
    }

    @JsonProperty(EVENT_ID)
    public String getEventId() {
        return eventId;
    }

    @JsonProperty(EVENT_TIME)
    public ZonedDateTime getEventTime() {
        return eventTime;
    }

    @JsonProperty(WEBHOOK_EVENT_ID)
    public String getWebhookEventId() {
        return webhookEventId;
    }

    @JsonProperty(CAUSE_EVENT_ID)
    public String getCauseEventId() {
        return causeEventId;
    }

    @JsonProperty(ROOT_EVENT_ID)
    public String getRootEventId() {
        return rootEventId;
    }

    @JsonProperty(WEBHOOK_ID)
    public String getWebhookId() {
        return webhookId;
    }

    @JsonProperty(URL)
    public Optional<String> getUrl() {
        return url;
    }

    @JsonProperty(REQUEST_BODY)
    public Optional<String> getRequestBody() {
        return requestBody;
    }

    @JsonProperty(REQUEST_HEADERS)
    public Map<String, List<String>> getRequestHeaders() {
        return requestHeaders;
    }

    @JsonProperty(RESPONSE_STATUS_CODE)
    public Optional<Integer> getResponseStatusCode() {
        return responseStatusCode;
    }

    @JsonProperty(RESPONSE_BODY)
    public Optional<String> getResponseBody() {
        return responseBody;
    }

    @JsonProperty(RESPONSE_HEADERS)
    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    @JsonProperty(ATTEMPT_COUNT)
    public int getAttemptCount() {
        return attemptCount;
    }

    @JsonProperty(METHOD)
    public Optional<String> getMethod() {
        return method;
    }

    @JsonProperty(LOG_MESSAGES)
    public List<String> getLogMessages() {
        return logMessages;
    }

    @JsonProperty(TAGS)
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty(RESPONSE)
    public Map<String, Object> getResponse() {
        return response;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }

}
