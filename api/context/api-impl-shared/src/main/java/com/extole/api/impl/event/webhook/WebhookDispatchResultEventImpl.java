package com.extole.api.impl.event.webhook;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.extole.api.event.webhook.WebhookDispatchResultEvent;
import com.extole.common.lang.ToString;

public class WebhookDispatchResultEventImpl implements WebhookDispatchResultEvent {
    private final String clientId;
    private final String webhookId;
    private final String eventTime;
    private final String url;
    private final int attemptCount;
    private final int configuredRetriesCount;
    private final String method;
    private final String requestBody;
    private final Map<String, List<String>> requestHeaders;
    private final Integer responseStatusCode;
    private final String responseBody;
    private final Map<String, List<String>> responseHeaders;
    private final String[] logMessages;
    private final String[] tags;

    public WebhookDispatchResultEventImpl(
        String clientId,
        String webhookId,
        String eventTime,
        String url,
        int attemptCount,
        int configuredRetriesCount,
        String method,
        String requestBody,
        Map<String, List<String>> requestHeaders,
        Integer responseStatusCode,
        String responseBody,
        Map<String, List<String>> responseHeaders,
        List<String> logMessages,
        List<String> tags) {
        this.clientId = clientId;
        this.webhookId = webhookId;
        this.eventTime = eventTime;
        this.url = url;
        this.attemptCount = attemptCount;
        this.configuredRetriesCount = configuredRetriesCount;
        this.method = method;
        this.requestBody = requestBody;
        this.requestHeaders = requestHeaders;
        this.responseStatusCode = responseStatusCode;
        this.responseBody = responseBody;
        this.responseHeaders = responseHeaders;
        this.logMessages = logMessages.toArray(new String[] {});
        this.tags = tags.toArray(new String[] {});
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getWebhookId() {
        return webhookId;
    }

    @Override
    public String getEventTime() {
        return eventTime;
    }

    @Nullable
    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getAttemptCount() {
        return attemptCount;
    }

    @Override
    public int getConfiguredRetriesCount() {
        return configuredRetriesCount;
    }

    @Nullable
    @Override
    public String getMethod() {
        return method;
    }

    @Nullable
    @Override
    public String getRequestBody() {
        return requestBody;
    }

    @Override
    public Map<String, List<String>> getRequestHeaders() {
        return requestHeaders;
    }

    @Nullable
    @Override
    public Integer getResponseStatusCode() {
        return responseStatusCode;
    }

    @Nullable
    @Override
    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    @Override
    public String[] getLogMessages() {
        return logMessages;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }

    private WebhookDispatchResultEventImpl(
        com.extole.event.webhook.dispatch.result.WebhookDispatchResultEvent event) {
        this.clientId = event.getClientId().getValue();
        this.webhookId = event.getWebhookId().getValue();
        this.eventTime = event.getEventTime().toString();
        this.url = event.getUrl().map(URL::toString).orElse(null);
        this.attemptCount = Integer.valueOf(event.getAttemptCount());
        this.configuredRetriesCount = Integer.valueOf(event.getConfiguredRetriesCount());
        this.method = event.getMethod().orElse(null);
        this.requestBody = event.getRequestBody().orElse(null);
        this.requestHeaders = event.getRequestHeaders();
        this.responseStatusCode = event.getResponseStatusCode().orElse(null);
        this.responseBody = event.getResponseBody().orElse(null);
        this.responseHeaders = event.getResponseHeaders();
        this.logMessages = event.getLogMessages().toArray(new String[] {});
        this.tags = event.getTags().toArray(new String[] {});
    }

    public static WebhookDispatchResultEventImpl
        newInstance(com.extole.event.webhook.dispatch.result.WebhookDispatchResultEvent event) {
        return new WebhookDispatchResultEventImpl(event);
    }
}
