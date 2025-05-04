package com.extole.client.rest.erasure;

import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErasureResponse {
    private static final String JSON_PROPERTY_STATUS = "status";
    private static final String JSON_PROPERTY_NOTE = "note";
    private static final String JSON_PROPERTY_CREATED_DATE = "created_date";

    private final ErasureStatus status;
    private final String note;
    private final ZonedDateTime createdDate;

    public ErasureResponse(
        @JsonProperty(JSON_PROPERTY_STATUS) ErasureStatus status,
        @Nullable @JsonProperty(JSON_PROPERTY_NOTE) String note,
        @JsonProperty(JSON_PROPERTY_CREATED_DATE) ZonedDateTime createdDate) {
        this.status = status;
        this.note = note;
        this.createdDate = createdDate;
    }

    @JsonProperty(JSON_PROPERTY_STATUS)
    public ErasureStatus getStatus() {
        return status;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_NOTE)
    public String getNote() {
        return note;
    }

    @JsonProperty(JSON_PROPERTY_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }
}
