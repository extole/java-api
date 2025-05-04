package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class EventStreamEventNameFilterUpdateRequest
    extends EventStreamFilterUpdateRequest {
    public static final String TYPE_EVENT_NAME = "EVENT_NAME";

    private static final String EVENT_NAMES = "event_names";

    private final Omissible<List<String>> eventNames;

    public EventStreamEventNameFilterUpdateRequest(
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

        public Builder withEventNames(List<String> eventNames) {
            this.eventNames = Omissible.of(eventNames);
            return this;
        }

        public EventStreamEventNameFilterUpdateRequest build() {
            return new EventStreamEventNameFilterUpdateRequest(eventNames);
        }
    }
}
