package com.extole.client.rest.campaign.controller.action.fire.as.person.identification;

import static com.extole.client.rest.campaign.controller.action.fire.as.person.FireAsPersonIdenticationType.EMAIL;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class EmailFireAsPersonIdentification extends FireAsPersonIdentification {
    static final String TYPE = "EMAIL";

    @JsonCreator
    public EmailFireAsPersonIdentification(@JsonProperty(JSON_VALUE) String value) {
        super(EMAIL, value);
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

        public EmailFireAsPersonIdentification build() {
            return new EmailFireAsPersonIdentification(value);
        }
    }
}
