package com.extole.event.api.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDispatcherRequest {

    public static final String DATA_EMAIL = "email";
    public static final String DATA_PARTNER_USER_ID = "partner_user_id";
    public static final String DATA_PERSON_ID = "person_id";
    public static final String DATA_EVENT_TIME = "event_time";

    private static final String JSON_EVENT_TIME = "event_time";
    private static final String JSON_EVENT_NAME = "event_name";
    private static final String JSON_DATA = "data";

    private final String eventName;
    private final Optional<String> eventTime;
    private final Map<String, Object> data;

    public EventDispatcherRequest(
        @JsonProperty(JSON_EVENT_NAME) String eventName,
        @JsonProperty(JSON_EVENT_TIME) Optional<String> eventTime,
        @Nullable @JsonProperty(JSON_DATA) Map<String, Object> data) {
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.data = data != null ? Collections.unmodifiableMap(data) : Collections.emptyMap();
    }

    @JsonProperty(JSON_EVENT_NAME)
    public String getEventName() {
        return eventName;
    }

    @JsonProperty(JSON_EVENT_TIME)
    public Optional<String> getEventTime() {
        return eventTime;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static EventRequestBuilder builder() {
        return new EventRequestBuilder();
    }

    public static final class EventRequestBuilder {
        private String eventName;
        private Optional<String> eventTime = Optional.empty();
        private Map<String, Object> data;

        private EventRequestBuilder() {
        }

        public EventRequestBuilder withEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public EventRequestBuilder withEventTime(String eventTime) {
            this.eventTime = Optional.ofNullable(eventTime);
            return this;
        }

        public EventRequestBuilder withData(Map<String, ? extends Object> data) {
            this.data = data != null ? Collections.unmodifiableMap(new HashMap<>(data)) : Collections.emptyMap();
            return this;
        }

        public EventDispatcherRequest build() {
            return new EventDispatcherRequest(eventName, eventTime, data);
        }

    }

}
