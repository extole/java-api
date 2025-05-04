package com.extole.consumer.rest.me;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeDataBulkUpdateRequest {

    // TODO rename to scope ENG-13506
    private static final String JSON_PROPERTY_TYPE = "type";
    private static final String JSON_PROPERTY_PARAMETERS = "parameters";

    private final Map<String, Object> personData;
    private final MeDataType type;

    @JsonCreator
    public MeDataBulkUpdateRequest(@JsonProperty(JSON_PROPERTY_TYPE) MeDataType type,
        @JsonProperty(JSON_PROPERTY_PARAMETERS) Map<String, Object> personData) {
        this.type = type;
        this.personData = personData == null ? Collections.emptyMap() : Collections.unmodifiableMap(personData);
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public MeDataType getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    public Map<String, Object> getData() {
        return personData;
    }
}
