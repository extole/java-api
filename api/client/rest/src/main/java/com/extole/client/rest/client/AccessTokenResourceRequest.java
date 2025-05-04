package com.extole.client.rest.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class AccessTokenResourceRequest {
    private static final String JSON_ID = "id";
    private static final String JSON_TYPE = "type";

    private final Id<?> id;
    private final AccessTokenResourceType type;

    public AccessTokenResourceRequest(
        @JsonProperty(JSON_ID) Id<?> id,
        @JsonProperty(JSON_TYPE) AccessTokenResourceType type) {
        this.id = id;
        this.type = type;
    }

    @JsonProperty(JSON_ID)
    public Id<?> getId() {
        return id;
    }

    @JsonProperty(JSON_TYPE)
    public AccessTokenResourceType getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
