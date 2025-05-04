package com.extole.webhook.dispatcher.rest.impl.dispatch;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.webhook.WebhookNotFoundException;
import com.extole.model.shared.webhook.BuiltWebhookCache;
import com.extole.webhook.dispatcher.rest.WebhookRestException;
import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchLocalEndpoints;
import com.extole.webhook.dispatcher.rest.dispatch.response.WebhookDispatchResponse;
import com.extole.webhook.event.service.WebhookEventService;
import com.extole.webhook.event.service.event.WebhookRecentEvent;

@Provider
public class WebhookDispatchLocalEndpointsImpl implements WebhookDispatchLocalEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final WebhookEventService webhookEventService;
    private final BuiltWebhookCache builtWebhookCache;
    private final WebhookDispatchRestMapper webhookDispatchRestMapper;

    @Inject
    public WebhookDispatchLocalEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        WebhookEventService webhookEventService,
        BuiltWebhookCache builtWebhookCache,
        WebhookDispatchRestMapper webhookDispatchRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.webhookEventService = webhookEventService;
        this.builtWebhookCache = builtWebhookCache;
        this.webhookDispatchRestMapper = webhookDispatchRestMapper;
    }

    @Override
    public List<WebhookDispatchResponse> getLocalWebhookDispatches(String accessToken, String webhookId,
        @Nullable Integer limit, @Nullable Integer offset, ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            builtWebhookCache.getWebhook(authorization.getClientId(), Id.valueOf(webhookId));
            WebhookEventService.WebhookRecentEventQueryBuilder webhookRecentEventQueryBuilder =
                webhookEventService.createRecentEventQuery(authorization, authorization.getIdentityId(),
                    Id.valueOf(webhookId));
            if (limit != null) {
                if (limit.intValue() > 0) {
                    webhookRecentEventQueryBuilder.withLimit(limit);
                } else {
                    return Collections.emptyList();
                }
            }
            if (offset != null && offset.intValue() >= 0) {
                webhookRecentEventQueryBuilder.withOffset(offset);
            }
            List<WebhookRecentEvent> events = webhookRecentEventQueryBuilder.query();
            return events.stream()
                .map(event -> webhookDispatchRestMapper.toWebhookDispatchResponse(event, timeZone))
                .collect(Collectors.toList());
        } catch (WebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(WebhookRestException.class)
                .withErrorCode(WebhookRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        }
    }
}
