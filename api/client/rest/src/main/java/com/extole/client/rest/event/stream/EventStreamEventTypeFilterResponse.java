package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.id.Id;

public class EventStreamEventTypeFilterResponse
    extends EventStreamFilterResponse {
    public static final String TYPE_EVENT_TYPE = "EVENT_TYPE";

    private static final String EVENT_TYPES = "event_types";

    private final List<ConsumerEventType> eventTypes;

    public EventStreamEventTypeFilterResponse(@JsonProperty(TYPE) EventFilterType type,
        @JsonProperty(ID) Id<?> id,
        @JsonProperty(EVENT_TYPES) List<ConsumerEventType> eventTypes) {
        super(type, id);
        this.eventTypes = eventTypes;
    }

    @JsonProperty(EVENT_TYPES)
    public List<ConsumerEventType> getEventTypes() {
        return eventTypes;
    }
}
