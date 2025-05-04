package com.extole.reporting.rest.fixup.filter;

import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventIdsFixupFilterResponse extends FixupFilterResponse {
    private static final String JSON_EVENT_IDS = "event_ids";

    private final Set<String> eventIds;

    @JsonCreator
    public EventIdsFixupFilterResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) FixupFilterType type,
        @JsonProperty(JSON_EVENT_IDS) Set<String> eventIds) {
        super(id, type);
        this.eventIds = eventIds != null ? Collections.unmodifiableSet(eventIds) : Collections.emptySet();
    }

    @JsonProperty(JSON_EVENT_IDS)
    public Set<String> getEventIds() {
        return eventIds;
    }
}
