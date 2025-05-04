package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class EventStreamEventTypeFilterUpdateRequest
    extends EventStreamFilterUpdateRequest {
    public static final String TYPE_EVENT_TYPE = "EVENT_TYPE";

    private static final String EVENT_TYPE = "event_types";

    private final Omissible<List<ConsumerEventType>> eventTypes;

    public EventStreamEventTypeFilterUpdateRequest(
        @JsonProperty(EVENT_TYPE) Omissible<List<ConsumerEventType>> eventTypes) {
        super(EventFilterType.EVENT_TYPE);
        this.eventTypes = eventTypes;
    }

    @JsonProperty(EVENT_TYPE)
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

        public Builder withEventTypes(List<ConsumerEventType> eventTypes) {
            this.eventTypes = Omissible.of(eventTypes);
            return this;
        }

        public EventStreamEventTypeFilterUpdateRequest build() {
            return new EventStreamEventTypeFilterUpdateRequest(eventTypes);
        }
    }
}
