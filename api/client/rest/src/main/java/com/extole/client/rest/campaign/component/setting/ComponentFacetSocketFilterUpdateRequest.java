package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class ComponentFacetSocketFilterUpdateRequest extends SocketFilterUpdateRequest {

    static final String TYPE = "FACET";

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";

    private final Omissible<String> name;
    private final Omissible<String> value;

    @JsonCreator
    public ComponentFacetSocketFilterUpdateRequest(
        @JsonProperty(JSON_NAME) Omissible<String> name,
        @JsonProperty(JSON_VALUE) Omissible<String> value) {
        super(SocketFilterType.FACET);
        this.name = name;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public Omissible<String> getValue() {
        return value;
    }

}
