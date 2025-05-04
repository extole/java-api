package com.extole.api.impl.event.client;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.extole.api.event.client.ClientEvent;
import com.extole.common.lang.ToString;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;
import com.extole.id.Id;

public class ClientEventImpl implements ClientEvent {

    private final String eventType;
    private final String eventId;
    private final String clientId;
    private final String eventTime;
    private final String name;
    private final String[] tags;
    private final String message;
    private final Map<String, DataValue> data;
    private final String level;
    private final Optional<String> userId;
    private final String scope;

    protected ClientEventImpl(com.extole.event.client.ClientEvent clientEvent) {
        this.eventType = clientEvent.getEventType().name();
        this.eventId = clientEvent.getEventId().getValue();
        this.clientId = clientEvent.getClientId().getValue();
        this.eventTime = ExtoleDateTimeFormatters.ISO_INSTANT.format(clientEvent.getEventTime());
        this.name = clientEvent.getName();
        this.tags = clientEvent.getTags().toArray(new String[] {});
        this.message = clientEvent.getMessage();
        this.level = clientEvent.getLevel().name();
        this.userId = clientEvent.getUserId().map(Id::getValue);
        this.scope = clientEvent.getScope().name();
        this.data = clientEvent.getData().entrySet().stream().collect(Collectors.toUnmodifiableMap(
            entry -> entry.getKey(),
            entry -> new DataValueImpl(entry.getValue().getValue(),
                entry.getValue().getType().name(),
                entry.getValue().getScope().name())));
    }

    public static ClientEventImpl newInstance(com.extole.event.client.ClientEvent clientEvent) {
        return new ClientEventImpl(clientEvent);
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getEventTime() {
        return eventTime;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Map<String, DataValue> getData() {
        return data;
    }

    @Override
    public String getLevel() {
        return level;
    }

    @Nullable
    @Override
    public String getUserId() {
        return userId.orElse(null);
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    private static final class DataValueImpl implements DataValue {

        private final String value;
        private final String type;
        private final String scope;

        private DataValueImpl(String value, String type, String scope) {
            this.value = value;
            this.type = type;
            this.scope = scope;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public String getScope() {
            return scope;
        }
    }

}
