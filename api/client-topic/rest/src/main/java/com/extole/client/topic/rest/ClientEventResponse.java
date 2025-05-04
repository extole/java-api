package com.extole.client.topic.rest;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ClientEventResponse {

    private static final String EVENT_ID = "event_id";
    private static final String EVENT_TIME = "event_time";
    private static final String NAME = "name";
    private static final String TAGS = "tags";
    private static final String MESSAGE = "message";
    private static final String DATA = "data";
    private static final String LEVEL = "level";
    private static final String SCOPE = "scope";

    private final String eventId;
    private final ZonedDateTime eventTime;
    private final String name;
    private final Set<String> tags;
    private final String message;
    private final Map<String, DataValue> data;
    private final Level level;
    private final Scope scope;

    public ClientEventResponse(@JsonProperty(EVENT_ID) String eventId,
        @JsonProperty(EVENT_TIME) ZonedDateTime eventTime,
        @JsonProperty(NAME) String name,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(MESSAGE) String message,
        @JsonProperty(DATA) Map<String, DataValue> data,
        @JsonProperty(LEVEL) Level level,
        @JsonProperty(SCOPE) Scope scope) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.name = name;
        this.tags = tags;
        this.message = message;
        this.data = data;
        this.level = level;
        this.scope = scope;
    }

    @JsonProperty(EVENT_ID)
    public String getEventId() {
        return eventId;
    }

    @JsonProperty(EVENT_TIME)
    public ZonedDateTime getEventTime() {
        return eventTime;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @JsonProperty(DATA)
    public Map<String, DataValue> getData() {
        return data;
    }

    @JsonProperty(LEVEL)
    public Level getLevel() {
        return level;
    }

    @JsonProperty(SCOPE)
    public Scope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
