package com.extole.client.rest.event.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EventStreamPersonIdFilterCreateRequest.class,
        name = EventStreamPersonIdFilterCreateRequest.TYPE_PERSON_ID),
    @JsonSubTypes.Type(value = EventStreamApplicationTypeFilterCreateRequest.class,
        name = EventStreamApplicationTypeFilterCreateRequest.TYPE_APPLICATION_TYPE),
    @JsonSubTypes.Type(value = EventStreamSandboxFilterCreateRequest.class,
        name = EventStreamSandboxFilterCreateRequest.TYPE_SANDBOX),
    @JsonSubTypes.Type(value = EventStreamEventNameFilterCreateRequest.class,
        name = EventStreamEventNameFilterCreateRequest.TYPE_EVENT_NAME),
    @JsonSubTypes.Type(value = EventStreamEventTypeFilterCreateRequest.class,
        name = EventStreamEventTypeFilterCreateRequest.TYPE_EVENT_TYPE),
})
public abstract class EventStreamFilterCreateRequest {

    protected static final String TYPE = "type";

    private final EventFilterType type;

    public EventStreamFilterCreateRequest(@JsonProperty(TYPE) EventFilterType type) {
        this.type = type;
    }

    @JsonProperty(TYPE)
    public EventFilterType getType() {
        return type;
    }
}
