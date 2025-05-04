package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.id.Id;

public class EventStreamEventNameFilterResponse
    extends EventStreamFilterResponse {
    public static final String TYPE_EVENT_NAME = "EVENT_NAME";

    private static final String EVENT_NAMES = "event_names";

    private final List<String> eventNames;

    public EventStreamEventNameFilterResponse(@JsonProperty(TYPE) EventFilterType type,
        @JsonProperty(ID) Id<?> id,
        @JsonProperty(EVENT_NAMES) List<String> eventNames) {
        super(type, id);
        this.eventNames = eventNames;
    }

    @JsonProperty(EVENT_NAMES)
    public List<String> getEventNames() {
        return eventNames;
    }
}
