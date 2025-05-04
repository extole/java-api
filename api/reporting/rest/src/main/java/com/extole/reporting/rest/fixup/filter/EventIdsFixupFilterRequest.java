package com.extole.reporting.rest.fixup.filter;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventIdsFixupFilterRequest {
    private static final String JSON_EVENT_IDS = "event_ids";

    private final Set<String> eventIds;

    @JsonCreator
    public EventIdsFixupFilterRequest(
        @Nullable @JsonProperty(JSON_EVENT_IDS) Set<String> eventIds) {
        this.eventIds = eventIds != null ? Collections.unmodifiableSet(eventIds) : null;
    }

    @Nullable
    @JsonProperty(JSON_EVENT_IDS)
    public Set<String> getEventIds() {
        return eventIds;
    }
}
