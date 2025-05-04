package com.extole.client.rest.event.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EventStreamPersonIdFilterResponse.class,
        name = EventStreamPersonIdFilterResponse.TYPE_PERSON_ID),
    @JsonSubTypes.Type(value = EventStreamApplicationTypeFilterResponse.class,
        name = EventStreamApplicationTypeFilterResponse.TYPE_APPLICATION_TYPE),
    @JsonSubTypes.Type(value = EventStreamSandboxFilterResponse.class,
        name = EventStreamSandboxFilterResponse.TYPE_SANDBOX),
    @JsonSubTypes.Type(value = EventStreamEventNameFilterResponse.class,
        name = EventStreamEventNameFilterResponse.TYPE_EVENT_NAME),
    @JsonSubTypes.Type(value = EventStreamEventTypeFilterResponse.class,
        name = EventStreamEventTypeFilterResponse.TYPE_EVENT_TYPE),
})
public abstract class EventStreamFilterResponse {

    protected static final String ID = "id";
    protected static final String TYPE = "type";

    private final EventFilterType type;
    private final Id<?> id;

    public EventStreamFilterResponse(@JsonProperty(TYPE) EventFilterType type,
        @JsonProperty(ID) Id<?> id) {
        this.type = type;
        this.id = id;
    }

    @JsonProperty(ID)
    public Id<?> getId() {
        return id;
    }

    @JsonProperty(TYPE)
    public EventFilterType getType() {
        return type;
    }
}
