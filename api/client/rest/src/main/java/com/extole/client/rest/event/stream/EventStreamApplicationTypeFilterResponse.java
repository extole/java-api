package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.id.Id;

public class EventStreamApplicationTypeFilterResponse
    extends EventStreamFilterResponse {
    public static final String TYPE_APPLICATION_TYPE = "APPLICATION_TYPE";

    private static final String APP_TYPES = "app_types";

    private final List<String> applicationTypes;

    public EventStreamApplicationTypeFilterResponse(@JsonProperty(TYPE) EventFilterType type,
        @JsonProperty(ID) Id<?> id,
        @JsonProperty(APP_TYPES) List<String> applicationTypes) {
        super(type, id);
        this.applicationTypes = applicationTypes;
    }

    @JsonProperty(APP_TYPES)
    public List<String> getApplicationTypes() {
        return applicationTypes;
    }
}
