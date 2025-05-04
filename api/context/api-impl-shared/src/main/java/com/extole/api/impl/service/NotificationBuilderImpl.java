package com.extole.api.impl.service;

import java.util.Locale;

import com.extole.api.service.NotificationBuilder;
import com.extole.authorization.service.ClientHandle;
import com.extole.event.client.ClientEvent.Level;
import com.extole.event.client.ClientEventBuilder;
import com.extole.event.client.ClientEventService;
import com.extole.event.client.Scope;
import com.extole.id.Id;

public class NotificationBuilderImpl implements NotificationBuilder {

    private static final String DEFAULT_MESSAGE = "No message provided";
    private final ClientEventBuilder eventBuilder;

    public NotificationBuilderImpl(ClientEventService clientEventService,
        Id<ClientHandle> clientId, String idType, Id<?> id) {
        this.eventBuilder = clientEventService.createClientEventBuilder()
            .withClientId(clientId)
            .withName(idType.toLowerCase(Locale.ENGLISH) + ":" + id.getValue())
            .addData("notification.source", idType.toLowerCase(Locale.ENGLISH) + ":" + id.getValue())
            .withMessage(DEFAULT_MESSAGE)
            .withLevel(Level.ERROR);
    }

    @Override
    public NotificationBuilder withName(String name) {
        eventBuilder.withName(name);
        return this;
    }

    @Override
    public NotificationBuilder withMessage(String message) {
        eventBuilder.withMessage(message);
        return this;
    }

    @Override
    public NotificationBuilder withLevel(String level) {
        eventBuilder.withLevel(Level.valueOf(level));
        return this;
    }

    @Override
    public NotificationBuilder withScope(String scope) {
        eventBuilder.withScope(Scope.valueOf(scope));
        return this;
    }

    @Override
    public NotificationBuilder addTag(String tag) {
        eventBuilder.addTags(tag);
        return this;
    }

    @Override
    public NotificationBuilder addParameter(String name, String value) {
        eventBuilder.addData(name, value);
        return this;
    }

    @Override
    public void send() {
        eventBuilder.send();
    }
}
