package com.extole.client.rest.campaign.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PersonIdFireAsPersonIdentification extends FireAsPersonIdentification {
    static final String TYPE = "PERSON_ID";

    @JsonCreator
    public PersonIdFireAsPersonIdentification(@JsonProperty(JSON_VALUE) String value) {
        super(FireAsPersonIdenticationType.PERSON_ID, value);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String value;

        private Builder() {
        }

        public Builder withValue(String value) {
            this.value = value;
            return this;
        }

        public PersonIdFireAsPersonIdentification build() {
            return new PersonIdFireAsPersonIdentification(value);
        }
    }
}
