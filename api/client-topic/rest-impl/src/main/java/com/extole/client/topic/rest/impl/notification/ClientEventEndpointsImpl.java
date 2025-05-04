package com.extole.client.topic.rest.impl.notification;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.authorization.service.Authorization;
import com.extole.client.topic.rest.ClientEventEndpoints;
import com.extole.client.topic.rest.ClientEventRequest;
import com.extole.client.topic.rest.ClientEventResponse;
import com.extole.client.topic.rest.ClientEventRestException;
import com.extole.common.event.kafka.producer.KafkaSendEventFailureException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.event.client.ClientEvent;
import com.extole.event.client.ClientEvent.DataValue;
import com.extole.event.client.ClientEvent.Level;
import com.extole.event.client.ClientEventBuilder;
import com.extole.event.client.ClientEventService;
import com.extole.event.client.Scope;
import com.extole.model.shared.user.UserCache;

@Provider
public class ClientEventEndpointsImpl implements ClientEventEndpoints {
    private static final int MIN_LENGTH = 2;

    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientEventService clientEventService;
    private final UserCache userCache;

    @Inject
    public ClientEventEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientEventService clientEventService,
        UserCache userCache) {
        this.authorizationProvider = authorizationProvider;
        this.clientEventService = clientEventService;
        this.userCache = userCache;
    }

    @Override
    public ClientEventResponse createClientEvent(String accessToken, ClientEventRequest request,
        ZoneId timeZone) throws UserAuthorizationRestException, ClientEventRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        ClientEventBuilder builder = clientEventService.createClientEventBuilder();
        if (Strings.isNullOrEmpty(request.getName()) || request.getName().length() < MIN_LENGTH) {
            throw RestExceptionBuilder.newBuilder(ClientEventRestException.class)
                .withErrorCode(ClientEventRestException.INVALID_NAME)
                .addParameter("name", request.getName())
                .build();
        }
        if (Strings.isNullOrEmpty(request.getMessage()) || request.getMessage().length() < MIN_LENGTH) {
            throw RestExceptionBuilder.newBuilder(ClientEventRestException.class)
                .withErrorCode(ClientEventRestException.INVALID_MESSAGE)
                .addParameter("message", request.getMessage())
                .build();
        }
        builder.withClientId(authorization.getClientId())
            .withLevel(Level.valueOf(request.getLevel().name()))
            .withScope(Scope.valueOf(request.getScope().name()))
            .withName(request.getName())
            .withMessage(request.getMessage());
        for (String tag : request.getTags()) {
            builder.addTags(tag);
        }
        request.getEventTime().ifPresent(eventTime -> builder.withEventTime(eventTime.toInstant()));
        userCache.getByAuthorization(authorization).map(user -> user.getId())
            .ifPresent(id -> builder.withUserId(id));
        for (Entry<String, com.extole.client.topic.rest.DataValue> entry : request.getData().entrySet()) {
            if (com.extole.client.topic.rest.DataValue.Type.ATTACHMENT.equals(entry.getValue().getType())) {
                builder.addAttachment(entry.getKey(), entry.getValue().getValue(),
                    Scope.valueOf(entry.getValue().getScope().name()));
            } else {
                builder.addData(entry.getKey(), entry.getValue().getValue(),
                    Scope.valueOf(entry.getValue().getScope().name()));
            }
        }
        try {
            ClientEvent event = builder.sendSynchronously();
            return new ClientEventResponse(event.getEventId().getValue(),
                ZonedDateTime.ofInstant(event.getEventTime(), timeZone),
                event.getName(),
                event.getTags(),
                event.getMessage(),
                transformData(event.getData()),
                com.extole.client.topic.rest.Level.valueOf(event.getLevel().name()),
                com.extole.client.topic.rest.Scope.valueOf(event.getScope().name()));
        } catch (KafkaSendEventFailureException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private Map<String, com.extole.client.topic.rest.DataValue> transformData(Map<String, DataValue> eventData) {
        Map<String, com.extole.client.topic.rest.DataValue> data = new HashMap<>();
        for (Entry<String, DataValue> entry : eventData.entrySet()) {
            data.put(entry.getKey(),
                new com.extole.client.topic.rest.DataValue(entry.getValue().getValue(),
                    com.extole.client.topic.rest.DataValue.Type
                        .valueOf(entry.getValue().getType().name()),
                    com.extole.client.topic.rest.Scope.valueOf(entry.getValue().getScope().name())));
        }
        return data;
    }
}
