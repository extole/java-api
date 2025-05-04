package com.extole.client.rest.person.v2;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.person.PersonDataScope;

public class PersonParametersBulkUpdateV2Request {

    private static final String JSON_PROPERTY_SCOPE = "scope";
    private static final String JSON_PROPERTY_PARAMETERS = "parameters";

    private final Map<String, Object> personData;
    private final PersonDataScope scope;

    @JsonCreator
    public PersonParametersBulkUpdateV2Request(@JsonProperty(JSON_PROPERTY_SCOPE) PersonDataScope scope,
        @JsonProperty(JSON_PROPERTY_PARAMETERS) Map<String, Object> personData) {
        this.scope = scope;
        this.personData = personData;
    }

    @JsonProperty(JSON_PROPERTY_SCOPE)
    public PersonDataScope getScope() {
        return scope;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    public Map<String, Object> getData() {
        return personData;
    }
}
