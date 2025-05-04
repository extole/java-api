package com.extole.client.rest.event.stream;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.id.Id;

public class EventStreamEventResponse {
    private static final String EVENT_ID = "event_id";
    private static final String EVENT_TIME = "event_time";
    private static final String EVENT_STREAM_ID = "event_stream_id";
    private static final String EVENT = "event";

    private final Id<?> eventId;
    private final ZonedDateTime eventTime;
    private final Id<?> eventStreamId;
    private final Object event;

    public EventStreamEventResponse(@JsonProperty(EVENT_ID) Id<?> eventId,
        @JsonProperty(EVENT_TIME) ZonedDateTime eventTime,
        @JsonProperty(EVENT_STREAM_ID) Id<?> eventStreamId,
        @JsonProperty(EVENT) Object event) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.eventStreamId = eventStreamId;
        this.event = event;
    }

    @JsonProperty(EVENT_ID)
    public Id<?> getEventId() {
        return eventId;
    }

    @JsonProperty(EVENT_TIME)
    public ZonedDateTime getEventTime() {
        return eventTime;
    }

    @JsonProperty(EVENT_STREAM_ID)
    public Id<?> getEventStreamId() {
        return eventStreamId;
    }

    @JsonProperty(EVENT)
    public Object getEvent() {
        return event;
    }
}
