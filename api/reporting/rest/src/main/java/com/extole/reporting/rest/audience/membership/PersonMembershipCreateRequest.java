package com.extole.reporting.rest.audience.membership;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.audience.Audience;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class PersonMembershipCreateRequest {

    private static final String AUDIENCE_ID = "audience_id";

    private final Id<Audience> audienceId;

    public PersonMembershipCreateRequest(@JsonProperty(AUDIENCE_ID) Id<Audience> audienceId) {
        this.audienceId = audienceId;
    }

    @JsonProperty(AUDIENCE_ID)
    public Id<Audience> getAudienceId() {
        return audienceId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Id<Audience> audienceId;

        private Builder() {
        }

        public Builder withAudienceId(Id<Audience> audienceId) {
            this.audienceId = audienceId;
            return this;
        }

        public PersonMembershipCreateRequest build() {
            return new PersonMembershipCreateRequest(audienceId);
        }
    }
}
