package com.extole.client.topic.rest;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ClientEventRequest {
    private static final String EVENT_TIME = "event_time";
    private static final String NAME = "name";
    private static final String TAGS = "tags";
    private static final String MESSAGE = "message";
    private static final String DATA = "data";
    private static final String LEVEL = "level";
    private static final String SCOPE = "scope";

    private final Optional<ZonedDateTime> eventTime;
    private final String name;
    private final List<String> tags;
    private final String message;
    private final Map<String, DataValue> data;
    private final Level level;
    private final Scope scope;

    public ClientEventRequest(
        @JsonProperty(EVENT_TIME) Optional<ZonedDateTime> eventTime,
        @JsonProperty(NAME) String name,
        @JsonProperty(TAGS) Optional<List<String>> tags,
        @JsonProperty(MESSAGE) String message,
        @JsonProperty(DATA) Optional<Map<String, DataValue>> data,
        @JsonProperty(LEVEL) Optional<Level> level,
        @JsonProperty(SCOPE) Optional<Scope> scope) {
        this.eventTime = eventTime;
        this.name = name;
        this.tags = tags.isPresent() ? tags.get() : Collections.emptyList();
        this.message = message;
        this.data = data.isPresent() ? data.get() : Collections.emptyMap();
        this.level = level.isPresent() ? level.get() : Level.INFO;
        this.scope = scope.isPresent() ? scope.get() : Scope.CLIENT_ADMIN;
    }

    @JsonProperty(EVENT_TIME)
    public Optional<ZonedDateTime> getEventTime() {
        return eventTime;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(TAGS)
    public List<String> getTags() {
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

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {

        private ZonedDateTime eventTime;
        private String name;
        private final List<String> tags = new ArrayList<>();
        private String message;
        private final Map<String, DataValue> data = new HashMap<>();
        private Level level;
        private Scope scope;

        private Builder() {
        }

        public Builder withEventTime(ZonedDateTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder addTag(String tag) {
            this.tags.add(tag);
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder addData(String key, DataValue dataValue) {
            data.put(key, dataValue);
            return this;
        }

        public Builder withLevel(Level level) {
            this.level = level;
            return this;
        }

        public Builder withScope(Scope scope) {
            this.scope = scope;
            return this;
        }

        public ClientEventRequest build() {
            return new ClientEventRequest(Optional.ofNullable(eventTime), name, Optional.of(tags), message,
                Optional.of(data), Optional.ofNullable(level), Optional.ofNullable(scope));
        }
    }
}
