package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class EventStreamEventTypeFilterCreateRequest
    extends EventStreamFilterCreateRequest {
    public static final String TYPE_EVENT_TYPE = "EVENT_TYPE";

    private static final String EVENT_TYPES = "event_types";

    private final Omissible<List<ConsumerEventType>> eventTypes;

    public EventStreamEventTypeFilterCreateRequest(
        @JsonProperty(EVENT_TYPES) Omissible<List<ConsumerEventType>> eventTypes) {
        super(EventFilterType.EVENT_TYPE);
        this.eventTypes = eventTypes;
    }

    @JsonProperty(EVENT_TYPES)
    public Omissible<List<ConsumerEventType>> getEventTypes() {
        return eventTypes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<List<ConsumerEventType>> eventTypes = Omissible.omitted();

        private Builder() {
        }

        public Builder withEventTypes(Omissible<List<ConsumerEventType>> eventTypes) {
            this.eventTypes = eventTypes;
            return this;
        }

        public EventStreamEventTypeFilterCreateRequest build() {
            return new EventStreamEventTypeFilterCreateRequest(eventTypes);
        }
    }
}
