package com.extole.client.rest.person.v4;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.audience.Audience;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class PersonAudienceMembershipV4Response {

    private static final String AUDIENCE_ID = "audience_id";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";

    private final Id<Audience> audienceId;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public PersonAudienceMembershipV4Response(
        @JsonProperty(AUDIENCE_ID) Id<Audience> audienceId,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate) {
        this.audienceId = audienceId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(AUDIENCE_ID)
    public Id<Audience> getAudienceId() {
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
