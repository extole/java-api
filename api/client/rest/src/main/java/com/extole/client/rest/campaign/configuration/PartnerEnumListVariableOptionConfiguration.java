package com.extole.client.rest.campaign.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PartnerEnumListVariableOptionConfiguration {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_DEFAULT = "default";

    private final String id;
    private final String name;
    private final String defaultValue;

    @JsonCreator
    public PartnerEnumListVariableOptionConfiguration(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DEFAULT) String defaultValue) {
        this.id = id;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_DEFAULT)
    public String getDefault() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
