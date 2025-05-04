package com.extole.client.rest.media;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class MediaAssetRequest {
    private static final String JSON_NAME = "name";

    private final String name;

    @JsonCreator
    public MediaAssetRequest(
        @Nullable @JsonProperty(JSON_NAME) String name) {
        this.name = name;
    }

    @Nullable
    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
