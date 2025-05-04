package com.extole.webhook.dispatcher.rest.impl.dispatch;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.rest.exception.ExtoleRestRuntimeException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.id.IdGenerator;
import com.extole.model.entity.webhook.Webhook;
import com.extole.model.entity.webhook.built.BuiltWebhook;
import com.extole.model.service.webhook.WebhookNotFoundException;
import com.extole.model.shared.webhook.BuiltWebhookCache;
import com.extole.webhook.dispatcher.rest.WebhookRestException;
import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchEndpoints;
import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchLocalEndpoints;
import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchRestException;
import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchType;
import com.extole.webhook.dispatcher.rest.dispatch.request.WebhookEventRequest;
import com.extole.webhook.dispatcher.rest.dispatch.response.ClientWebhookDispatchResponse;
import com.extole.webhook.dispatcher.rest.dispatch.response.ConsumerWebhookDispatchResponse;
import com.extole.webhook.dispatcher.rest.dispatch.response.RewardWebhookDispatchResponse;
import com.extole.webhook.dispatcher.rest.dispatch.response.WebhookDispatchResponse;
import com.extole.webhook.dispatcher.rest.dispatch.result.WebhookDispatchResultResponse;
import com.extole.webhook.dispatcher.rest.impl.dispatch.result.WebhookDispatchResultRestMapper;
import com.extole.webhook.dispatcher.service.PartnerWebhookDispatchBuilder;
import com.extole.webhook.dispatcher.service.WebhookDispatchFailedException;
import com.extole.webhook.dispatcher.service.WebhookDispatchResult;
import com.extole.webhook.dispatcher.service.impl.dispatch.sender.PartnerWebhookDispatcher;
import com.extole.webhook.event.service.WebhookEventSendException;
import com.extole.webhook.event.service.WebhookEventService;
import com.extole.webhook.event.service.WebhookEventType;
import com.extole.webhook.event.service.event.WebhookRecentEvent;

@Provider
public class WebhookDispatchEndpointsImpl implements WebhookDispatchEndpoints {

    private static final IdGenerator ID_GENERATOR = new IdGenerator();
    private static final Logger LOG = LoggerFactory.getLogger(WebhookDispatchEndpointsImpl.class);

    private final ClientAuthorizationProvider authorizationProvider;
    private final BuiltWebhookCache builtWebhookCache;
    private final WebhookDispatchLocalEndpointsProvider endpointsProvider;
    private final WebhookEventService webhookEventService;
    private final WebhookDispatchRestMapper webhookDispatchRestMapper;
    private final PartnerWebhookDispatcher partnerWebhookDispatcher;
    private final WebhookDispatchResultRestMapper webhookDispatchResultRestMapper;

    @Inject
    public WebhookDispatchEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        BuiltWebhookCache builtWebhookCache,
        WebhookDispatchLocalEndpointsProvider endpointsProvider,
        WebhookDispatchResultRestMapper webhookDispatchResultRestMapper,
        WebhookEventService webhookEventService,
        WebhookDispatchRestMapper webhookDispatchRestMapper,
        PartnerWebhookDispatcher partnerWebhookDispatcher) {
        this.authorizationProvider = authorizationProvider;
        this.builtWebhookCache = builtWebhookCache;
        this.endpointsProvider = endpointsProvider;
        this.webhookEventService = webhookEventService;
        this.webhookDispatchRestMapper = webhookDispatchRestMapper;
        this.webhookDispatchResultRestMapper = webhookDispatchResultRestMapper;
        this.partnerWebhookDispatcher = partnerWebhookDispatcher;
    }

    @Override
    public List<WebhookDispatchResponse> getWebhookDispatches(String accessToken, String webhookId,
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

        List<WebhookDispatchResponse> dispatches = new ArrayList<>();
        for (WebhookDispatchLocalEndpoints endpoints : endpointsProvider.getLocalEndpoints()) {
            try {
                List<WebhookDispatchResponse> localDispatches =
                    endpoints.getLocalWebhookDispatches(accessToken, webhookId, limit, offset, timeZone);
                dispatches.addAll(localDispatches);
            } catch (ExtoleRestRuntimeException e) {
                LOG.error("Unexpected error while retrieving notifications for local cluster " +
                    "for client_id: {}, webhook_id: {}, error_code: {}, error_parameters: {}",
                    authorization.getClientId(), webhookId, e.getErrorCode(), e.getParameters(), e);
            }
        }

        Set<String> eventIds = new HashSet<>();
        dispatches.removeIf(event -> !eventIds.add(event.getEventId()));

        dispatches.sort(Collections.reverseOrder(
            WebhookDispatchEndpointsImpl.WebhookDispatchComparator.WEBHOOK_DISPATCH_COMPARATOR_INSTANCE));

        if (limit != null && dispatches.size() > limit.intValue()) {
            dispatches = dispatches.subList(0, limit.intValue());
        }

        return dispatches.stream()
            .map(dispatch -> toDateAdjustedResponse(dispatch, timeZone))
            .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    // TODO remove this method as part of ENG-15677
    private WebhookDispatchResponse toDateAdjustedResponse(WebhookDispatchResponse dispatchResponse, ZoneId timeZone) {
        if (dispatchResponse instanceof ClientWebhookDispatchResponse) {
            ClientWebhookDispatchResponse clientWebhookDispatchResponse =
                (ClientWebhookDispatchResponse) dispatchResponse;

            Map<String, Object> event = new HashMap<>(clientWebhookDispatchResponse.getEvent());
            Map<String, Object> clientEvent = new HashMap<>((Map<String, Object>) event.get("client_event"));

            ZonedDateTime clientEventZonedDateTime = ObjectMapperProvider.getConfiguredInstance()
                .convertValue(clientEvent.get("event_time"),
                    new TypeReference<Instant>() {})
                .atZone(timeZone);

            clientEvent.put("event_time", clientEventZonedDateTime);
            event.put("client_event", clientEvent);

            return ClientWebhookDispatchResponse
                .builder(clientWebhookDispatchResponse)
                .withEventTime(dispatchResponse.getEventTime().withZoneSameInstant(timeZone))
                .withEvent(event)
                .build();
        }
        if (dispatchResponse instanceof ConsumerWebhookDispatchResponse) {
            return ConsumerWebhookDispatchResponse
                .builder((ConsumerWebhookDispatchResponse) dispatchResponse)
                .withEventTime(dispatchResponse.getEventTime().withZoneSameInstant(timeZone))
                .build();
        }
        if (dispatchResponse instanceof RewardWebhookDispatchResponse) {
            return RewardWebhookDispatchResponse
                .builder((RewardWebhookDispatchResponse) dispatchResponse)
                .withEventTime(dispatchResponse.getEventTime().withZoneSameInstant(timeZone))
                .build();
        }

        throw new IllegalStateException("No possible to perform date adjustment for the type "
            + dispatchResponse.getClass().getSimpleName());
    }

    @Override
    public WebhookDispatchResponse sendWebhookEvent(String accessToken, WebhookEventRequest request,
        ZoneId timeZone) throws UserAuthorizationRestException, WebhookRestException, WebhookDispatchRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        if (StringUtils.isBlank(request.getClientId())) {
            throw RestExceptionBuilder.newBuilder(WebhookDispatchRestException.class)
                .withErrorCode(WebhookDispatchRestException.MISSING_CLIENT_ID)
                .build();
        }
        if (!authorization.getClientId().getValue().equals(request.getClientId())) {
            throw RestExceptionBuilder.newBuilder(WebhookDispatchRestException.class)
                .withErrorCode(WebhookDispatchRestException.REQUEST_CLIENT_ID_INVALID)
                .build();
        }
        if (StringUtils.isBlank(request.getWebhookId())) {
            throw RestExceptionBuilder.newBuilder(WebhookDispatchRestException.class)
                .withErrorCode(WebhookDispatchRestException.MISSING_WEBHOOK_ID)
                .build();
        }

        Id<ClientHandle> clientId = Id.valueOf(request.getClientId());
        Id<Webhook> webhookId = Id.valueOf(request.getWebhookId());
        BuiltWebhook webhook;
        try {
            webhook = builtWebhookCache.getWebhook(clientId, webhookId);
        } catch (WebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(WebhookRestException.class)
                .withErrorCode(WebhookRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", e.getWebhookId())
                .withCause(e)
                .build();
        }

        if (!webhook.getType().equals(toWebhookType(request.getType()))) {
            throw RestExceptionBuilder.newBuilder(WebhookRestException.class)
                .withErrorCode(WebhookRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhook.getId())
                .build();
        }

        try {
            Id<?> existingEventId = Id.valueOf(request.getEventId().orElse("unknown"));
            Map<String, Object> requestEvent = request.getEvent();

            WebhookRecentEvent event = webhookEventService.sendEvent(authorization, existingEventId, webhookId,
                WebhookEventType.valueOf(request.getType().name()), requestEvent);
            return webhookDispatchRestMapper.toWebhookDispatchResponse(event, timeZone);
        } catch (WebhookEventSendException e) {
            throw RestExceptionBuilder.newBuilder(WebhookDispatchRestException.class)
                .withErrorCode(WebhookDispatchRestException.WEBHOOK_DISPATCH_FAILED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public WebhookDispatchResultResponse sendSyncWebhookEvent(String accessToken,
        WebhookEventRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, WebhookRestException, WebhookDispatchRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        if (StringUtils.isBlank(request.getClientId())) {
            throw RestExceptionBuilder.newBuilder(WebhookDispatchRestException.class)
                .withErrorCode(WebhookDispatchRestException.MISSING_CLIENT_ID)
                .build();
        }
        if (!authorization.getClientId().getValue().equals(request.getClientId())) {
            throw RestExceptionBuilder.newBuilder(WebhookDispatchRestException.class)
                .withErrorCode(WebhookDispatchRestException.REQUEST_CLIENT_ID_INVALID)
                .build();
        }
        if (StringUtils.isBlank(request.getWebhookId())) {
            throw RestExceptionBuilder.newBuilder(WebhookDispatchRestException.class)
                .withErrorCode(WebhookDispatchRestException.MISSING_WEBHOOK_ID)
                .build();
        }
        Id<ClientHandle> clientId = Id.valueOf(request.getClientId());
        BuiltWebhook webhook;
        try {
            webhook = builtWebhookCache.getWebhook(clientId, Id.valueOf(request.getWebhookId()));
        } catch (WebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(WebhookRestException.class)
                .withErrorCode(WebhookRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", e.getWebhookId())
                .withCause(e)
                .build();
        }

        if (!webhook.getType().equals(toWebhookType(request.getType()))) {
            throw RestExceptionBuilder.newBuilder(WebhookRestException.class)
                .withErrorCode(WebhookRestException.WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhook.getId())
                .build();
        }

        try {
            PartnerWebhookDispatchBuilder builder = partnerWebhookDispatcher.createDispatchBuilder(webhook)
                .withWebhookEventId(ID_GENERATOR.generateId())
                .withCauseEventId(ID_GENERATOR.generateId())
                .withRootEventId(ID_GENERATOR.generateId())
                .withWebhookEventTime(Instant.now())
                .withWebhookEventCauseEventSequence(0)
                .withAttemptCount(0)
                .addLogMessage(
                    "Manual dispatch with initial webhook event id: " + request.getEventId().orElse("<missing>"));

            WebhookDispatchResult result = builder.performDispatch();
            return webhookDispatchResultRestMapper.toWebhookDispatchResultResponse(result, timeZone);
        } catch (WebhookDispatchFailedException e) {
            throw RestExceptionBuilder.newBuilder(WebhookDispatchRestException.class)
                .withErrorCode(WebhookDispatchRestException.WEBHOOK_DISPATCH_FAILED)
                .withCause(e)
                .build();
        }
    }

    private com.extole.model.entity.webhook.WebhookType toWebhookType(WebhookDispatchType webhookDispatchType) {
        switch (webhookDispatchType) {
            case CLIENT:
                return com.extole.model.entity.webhook.WebhookType.CLIENT;
            case REWARD:
                return com.extole.model.entity.webhook.WebhookType.REWARD;
            case CONSUMER:
                return com.extole.model.entity.webhook.WebhookType.GENERIC;
            case PARTNER:
                return com.extole.model.entity.webhook.WebhookType.PARTNER;
            default:
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withCause(new WebhookDispatchEndpointsRuntimeException(
                        "Unknown webhook dispatch type=" + webhookDispatchType))
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .build();
        }
    }

    private static final class WebhookDispatchComparator implements Comparator<WebhookDispatchResponse> {

        private static final WebhookDispatchComparator WEBHOOK_DISPATCH_COMPARATOR_INSTANCE =
            new WebhookDispatchComparator();

        private WebhookDispatchComparator() {
        }

        @Override
        public int compare(WebhookDispatchResponse firstDispatch, WebhookDispatchResponse secondDispatch) {
            ZonedDateTime firstEventTime = firstDispatch.getEventTime();
            ZonedDateTime secondEventTime = secondDispatch.getEventTime();

            return firstEventTime.compareTo(secondEventTime);
        }
    }

    public static final class WebhookDispatchEndpointsRuntimeException extends RuntimeException {

        public WebhookDispatchEndpointsRuntimeException(String message) {
            super(message);
        }

    }

}
