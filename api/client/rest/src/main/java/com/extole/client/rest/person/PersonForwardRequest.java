package com.extole.client.rest.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonForwardRequest {

    private static final String FORWARD_TO_PROFILE_ID_JSON = "forward_to_profile_id";

    private final String forwardToProfileId;

    @JsonCreator
    public PersonForwardRequest(
        @JsonProperty(FORWARD_TO_PROFILE_ID_JSON) String forwardToProfileId) {
        this.forwardToProfileId = forwardToProfileId;
    }

    @JsonProperty(FORWARD_TO_PROFILE_ID_JSON)
    public String getForwardToProfileId() {
        return forwardToProfileId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String forwardToProfileId;

        private Builder() {
        }

        public Builder withForwardToProfileId(String forwardToProfileId) {
            this.forwardToProfileId = forwardToProfileId;
            return this;
        }

        public PersonForwardRequest build() {
            return new PersonForwardRequest(forwardToProfileId);
        }
    }
}
