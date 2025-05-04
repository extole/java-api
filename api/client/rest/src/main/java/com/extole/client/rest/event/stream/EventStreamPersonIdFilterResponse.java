package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.id.Id;

public class EventStreamPersonIdFilterResponse
    extends EventStreamFilterResponse {
    public static final String TYPE_PERSON_ID = "PERSON_ID";

    private static final String PERSON_IDS = "person_ids";

    private final List<Id<?>> personIds;

    public EventStreamPersonIdFilterResponse(@JsonProperty(TYPE) EventFilterType type,
        @JsonProperty(ID) Id<?> id,
        @JsonProperty(PERSON_IDS) List<Id<?>> personIds) {
        super(type, id);
        this.personIds = personIds;
    }

    @JsonProperty(PERSON_IDS)
    public List<Id<?>> getPersonIds() {
        return personIds;
    }
}
