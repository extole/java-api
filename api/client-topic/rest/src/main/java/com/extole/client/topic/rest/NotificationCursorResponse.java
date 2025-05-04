package com.extole.client.topic.rest;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class NotificationCursorResponse {

    private static final String LAST_READ_EVENT_TIME = "last_read_event_time";

    private final ZonedDateTime lastReadEventTime;

    public NotificationCursorResponse(@JsonProperty(LAST_READ_EVENT_TIME) ZonedDateTime lastReadEventTime) {
        this.lastReadEventTime = lastReadEventTime;
    }

    @JsonProperty(LAST_READ_EVENT_TIME)
    public ZonedDateTime getDateTime() {
        return lastReadEventTime;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
