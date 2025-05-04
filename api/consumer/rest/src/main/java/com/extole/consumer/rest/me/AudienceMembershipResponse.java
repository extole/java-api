package com.extole.consumer.rest.me;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class AudienceMembershipResponse {

    private static final String AUDIENCE_ID = "audience_id";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";

    private final String audienceId;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public AudienceMembershipResponse(
        @JsonProperty(AUDIENCE_ID) String audienceId,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate) {
        this.audienceId = audienceId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(AUDIENCE_ID)
    public String getAudienceId() {
        return audienceId;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
