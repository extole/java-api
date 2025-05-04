package com.extole.webhook.dispatcher.rest.impl.dispatch.result;

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
import com.extole.webhook.dispatch.result.event.service.WebhookDispatchResultEventService;
import com.extole.webhook.dispatch.result.event.service.WebhookDispatchResultRecentEvent;
import com.extole.webhook.dispatcher.rest.WebhookRestException;
import com.extole.webhook.dispatcher.rest.dispatch.result.WebhookDispatchResultLocalEndpoints;
import com.extole.webhook.dispatcher.rest.dispatch.result.WebhookDispatchResultResponse;

@Provider
public class WebhookDispatchResultLocalEndpointsImpl implements WebhookDispatchResultLocalEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final WebhookDispatchResultEventService webhookDispatchResultEventService;
    private final BuiltWebhookCache builtWebhookCache;
    private final WebhookDispatchResultRestMapper webhookDispatchResultRestMapper;

    @Inject
    public WebhookDispatchResultLocalEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        WebhookDispatchResultEventService webhookDispatchResultEventService,
        BuiltWebhookCache builtWebhookCache,
        WebhookDispatchResultRestMapper webhookDispatchResultRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.webhookDispatchResultEventService = webhookDispatchResultEventService;
        this.builtWebhookCache = builtWebhookCache;
        this.webhookDispatchResultRestMapper = webhookDispatchResultRestMapper;
    }

    @Override
    public List<WebhookDispatchResultResponse> getLocalWebhookDispatchResults(String accessToken,
        String webhookId, @Nullable Integer limit, @Nullable Integer offset, ZoneId timeZone)
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

        WebhookDispatchResultEventService.WebhookDispatchResultRecentEventQueryBuilder
                webhookDispatchResultRecentEventQueryBuilder =
            webhookDispatchResultEventService
                .createRecentEventQuery(authorization.getClientId(), authorization.getIdentityId(),
                    Id.valueOf(webhookId));
        if (limit != null) {
            if (limit.intValue() > 0) {
                webhookDispatchResultRecentEventQueryBuilder.withLimit(limit);
            } else {
                return Collections.emptyList();
            }
        }
        if (offset != null && offset.intValue() >= 0) {
            webhookDispatchResultRecentEventQueryBuilder.withOffset(offset);
        }
        List<WebhookDispatchResultRecentEvent> events = webhookDispatchResultRecentEventQueryBuilder.query();
        return events.stream()
            .map(event -> webhookDispatchResultRestMapper.toWebhookDispatchResultResponse(event, timeZone))
            .collect(Collectors.toList());
    }

}
