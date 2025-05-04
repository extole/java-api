package com.extole.client.rest.media;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class MediaAssetResponse {
    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_UPDATED_DATE = "updated_date";

    private final String id;
    private final String name;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public MediaAssetResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return this.id;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return this.name;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    @JsonProperty(JSON_UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
