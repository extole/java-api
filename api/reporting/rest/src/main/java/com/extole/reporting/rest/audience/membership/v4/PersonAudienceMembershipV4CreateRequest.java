package com.extole.reporting.rest.audience.membership.v4;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.audience.Audience;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

@Schema
public class PersonAudienceMembershipV4CreateRequest {

    private static final String AUDIENCE_ID = "audience_id";

    private final Id<Audience> audienceId;

    public PersonAudienceMembershipV4CreateRequest(@JsonProperty(AUDIENCE_ID) Id<Audience> audienceId) {
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

        public PersonAudienceMembershipV4CreateRequest build() {
            return new PersonAudienceMembershipV4CreateRequest(audienceId);
        }

    }

}
