package com.extole.api.event.webhook;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface WebhookDispatchResultEvent {

    String getClientId();

    String getWebhookId();

    @Nullable
    String getUrl();

    String getEventTime();

    int getAttemptCount();

    int getConfiguredRetriesCount();

    @Nullable
    String getMethod();

    @Nullable
    String getRequestBody();

    Map<String, List<String>> getRequestHeaders();

    @Nullable
    Integer getResponseStatusCode();

    @Nullable
    String getResponseBody();

    Map<String, List<String>> getResponseHeaders();

    String[] getLogMessages();

    String[] getTags();

}
