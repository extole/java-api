package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class EventStreamEventNameFilterCreateRequest
    extends EventStreamFilterCreateRequest {
    public static final String TYPE_EVENT_NAME = "EVENT_NAME";

    private static final String EVENT_NAMES = "event_names";

    private final Omissible<List<String>> eventNames;

    public EventStreamEventNameFilterCreateRequest(
        @JsonProperty(EVENT_NAMES) Omissible<List<String>> eventNames) {
        super(EventFilterType.EVENT_NAME);
        this.eventNames = eventNames;
    }

    @JsonProperty(EVENT_NAMES)
    public Omissible<List<String>> getEventNames() {
        return eventNames;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<List<String>> eventNames = Omissible.omitted();

        private Builder() {
        }

        public Builder withEventName(Omissible<List<String>> eventName) {
            this.eventNames = eventName;
            return this;
        }

        public EventStreamEventNameFilterCreateRequest build() {
            return new EventStreamEventNameFilterCreateRequest(eventNames);
        }
    }
}
