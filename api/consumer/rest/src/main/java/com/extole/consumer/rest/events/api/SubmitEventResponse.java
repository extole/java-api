package com.extole.consumer.rest.events.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SubmitEventResponse {

    private static final String EVENT_ID = "event_id";
    private static final String ID = "id";
    private final String id;

    @JsonCreator
    public SubmitEventResponse(@JsonProperty(ID) String id) {
        this.id = id;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @Deprecated // TODO remove deprecated members ENG-12142
    @JsonProperty(EVENT_ID)
    public String getEventId() {
        return id;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
