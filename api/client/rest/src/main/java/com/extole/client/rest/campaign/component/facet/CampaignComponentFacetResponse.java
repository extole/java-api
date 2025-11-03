package com.extole.client.rest.campaign.component.facet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class CampaignComponentFacetResponse {

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";

    private final String name;
    private final String value;

    @JsonCreator
    public CampaignComponentFacetResponse(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_VALUE) String value) {
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

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
