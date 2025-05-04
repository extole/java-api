package com.extole.client.rest.event.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EventStreamPersonIdFilterUpdateRequest.class,
        name = EventStreamPersonIdFilterUpdateRequest.TYPE_PERSON_ID),
    @JsonSubTypes.Type(value = EventStreamApplicationTypeFilterUpdateRequest.class,
        name = EventStreamApplicationTypeFilterUpdateRequest.TYPE_APPLICATION_TYPE),
    @JsonSubTypes.Type(value = EventStreamSandboxFilterUpdateRequest.class,
        name = EventStreamSandboxFilterUpdateRequest.TYPE_SANDBOX),
    @JsonSubTypes.Type(value = EventStreamEventNameFilterUpdateRequest.class,
        name = EventStreamEventNameFilterUpdateRequest.TYPE_EVENT_NAME),
    @JsonSubTypes.Type(value = EventStreamEventTypeFilterUpdateRequest.class,
        name = EventStreamEventTypeFilterUpdateRequest.TYPE_EVENT_TYPE),
})
public abstract class EventStreamFilterUpdateRequest {

    protected static final String TYPE = "type";

    private final EventFilterType type;

    public EventStreamFilterUpdateRequest(@JsonProperty(TYPE) EventFilterType type) {
        this.type = type;
    }

    @JsonProperty(TYPE)
    public EventFilterType getType() {
        return type;
    }
}
