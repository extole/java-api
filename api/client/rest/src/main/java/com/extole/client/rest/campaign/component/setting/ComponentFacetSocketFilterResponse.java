package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ComponentFacetSocketFilterResponse extends SocketFilterResponse {

    static final String TYPE = "FACET";

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";

    private final String name;
    private final String value;

    @JsonCreator
    public ComponentFacetSocketFilterResponse(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_VALUE) String value) {
        super(SocketFilterType.FACET);
        this.name = name;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public String getValue() {
        return value;
    }

}
