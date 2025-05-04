package com.extole.webhook.dispatcher.rest.impl.dispatch.result;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableList;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.webhook.WebhookNotFoundException;
import com.extole.model.shared.webhook.BuiltWebhookCache;
import com.extole.webhook.dispatcher.rest.WebhookRestException;
import com.extole.webhook.dispatcher.rest.dispatch.result.WebhookDispatchResultEndpoints;
import com.extole.webhook.dispatcher.rest.dispatch.result.WebhookDispatchResultLocalEndpoints;
import com.extole.webhook.dispatcher.rest.dispatch.result.WebhookDispatchResultResponse;

@Provider
public class WebhookDispatchResultEndpointsImpl implements WebhookDispatchResultEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final BuiltWebhookCache builtWebhookCache;
    private final WebhookDispatchResultLocalEndpointsProvider endpointsProvider;

    @Inject
    public WebhookDispatchResultEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        BuiltWebhookCache builtWebhookCache,
        WebhookDispatchResultLocalEndpointsProvider endpointsProvider) {
        this.authorizationProvider = authorizationProvider;
        this.builtWebhookCache = builtWebhookCache;
        this.endpointsProvider = endpointsProvider;
    }

    @Override
    public List<WebhookDispatchResultResponse> getWebhookDispatchResults(String accessToken, String webhookId,
        @Nullable Integer limit, @Nullable Integer offset, ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            builtWebhookCache.getWebhook(authorization.getClientId(), Id.valueOf(webhookId));
        } catch (WebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(WebhookRestException.class)
                .withErrorCode(WebhookRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        }

        List<WebhookDispatchResultResponse> results = new ArrayList<>();
        for (WebhookDispatchResultLocalEndpoints endpoints : endpointsProvider.getClusterEndpoints()) {
            List<WebhookDispatchResultResponse> localResults =
                endpoints.getLocalWebhookDispatchResults(accessToken, webhookId, limit, offset, timeZone);
            results.addAll(localResults);
        }

        Set<String> eventIds = new HashSet<>();
        results.removeIf(event -> !eventIds.add(event.getEventId()));

        results.sort(Collections.reverseOrder(
            WebhookDispatchResultComparator.WEBHOOK_DISPATCH_RESULT_COMPARATOR_INSTANCE));

        if (limit != null && results.size() > limit.intValue()) {
            results = results.subList(0, limit.intValue());
        }

        return results.stream()
            .map(result -> toDateAdjustedResponse(result, timeZone))
            .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    // TODO remove this method as part of ENG-15677
    private WebhookDispatchResultResponse toDateAdjustedResponse(WebhookDispatchResultResponse result,
        ZoneId timeZone) {
        return new WebhookDispatchResultResponse(result.getEventId(),
            result.getEventTime().withZoneSameInstant(timeZone),
            result.getWebhookEventId(),
            result.getCauseEventId(),
            result.getRootEventId(),
            result.getWebhookId(),
            result.getUrl(),
            result.getRequestBody(),
            result.getRequestHeaders(),
            result.getResponseStatusCode(),
            result.getResponseBody(),
            result.getResponseHeaders(),
            result.getAttemptCount(),
            result.getMethod(),
            result.getLogMessages(),
            result.getTags(),
            result.getResponse());
    }

    private static final class WebhookDispatchResultComparator implements Comparator<WebhookDispatchResultResponse> {

        private static final WebhookDispatchResultComparator WEBHOOK_DISPATCH_RESULT_COMPARATOR_INSTANCE =
            new WebhookDispatchResultComparator();

        private WebhookDispatchResultComparator() {
        }

        @Override
        public int compare(WebhookDispatchResultResponse firstResult, WebhookDispatchResultResponse secondResult) {
            ZonedDateTime firstEventTime = firstResult.getEventTime();
            ZonedDateTime secondEventTime = secondResult.getEventTime();

            return firstEventTime.compareTo(secondEventTime);
        }

    }

}
