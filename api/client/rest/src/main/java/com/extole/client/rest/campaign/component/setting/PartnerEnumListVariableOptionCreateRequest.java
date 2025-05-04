package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PartnerEnumListVariableOptionCreateRequest {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DEFAULT = "default";

    private final String id;
    private final String name;
    private final String defaultValue;

    @JsonCreator
    public PartnerEnumListVariableOptionCreateRequest(@JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(DEFAULT) String defaultValue) {
        this.id = id;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(DEFAULT)
    public String getDefault() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
