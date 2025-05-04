package com.extole.client.rest.person.memberships;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonMembershipResponse {

    private static final String AUDIENCE_ID = "audience_id";
    private static final String AUDIENCE_NAME = "audience_name";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";

    private final String audienceId;
    private final String audienceName;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public PersonMembershipResponse(
        @JsonProperty(AUDIENCE_ID) String audienceId,
        @JsonProperty(AUDIENCE_NAME) String audienceName,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate) {
        this.audienceId = audienceId;
        this.audienceName = audienceName;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(AUDIENCE_ID)
    public String getAudienceId() {
        return audienceId;
    }

    @JsonProperty(AUDIENCE_NAME)
    public String getAudienceName() {
        return audienceName;
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

    public static Builder builder() {
        return new PersonMembershipResponse.Builder();
    }

    public static final class Builder {
        private String audienceId;
        private String audienceName;
        private ZonedDateTime createdDate;
        private ZonedDateTime updatedDate;

        private Builder() {
        }

        public Builder withAudienceId(String audienceId) {
            this.audienceId = audienceId;
            return this;
        }

        public Builder withAudienceName(String audienceName) {
            this.audienceName = audienceName;
            return this;
        }

        public Builder withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withUpdatedDate(ZonedDateTime updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        public PersonMembershipResponse build() {
            return new PersonMembershipResponse(audienceId, audienceName, createdDate, updatedDate);
        }
    }
}
