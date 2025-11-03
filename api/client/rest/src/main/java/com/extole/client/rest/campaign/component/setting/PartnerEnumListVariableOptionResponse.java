package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PartnerEnumListVariableOptionResponse {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";

    private final String id;
    private final String name;

    @JsonCreator
    public PartnerEnumListVariableOptionResponse(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) String name) {
        this.id = id;
        this.name = name;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
