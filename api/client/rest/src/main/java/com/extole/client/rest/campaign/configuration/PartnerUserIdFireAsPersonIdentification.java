package com.extole.client.rest.campaign.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PartnerUserIdFireAsPersonIdentification extends FireAsPersonIdentification {
    static final String TYPE = "PARTNER_USER_ID";

    @JsonCreator
    public PartnerUserIdFireAsPersonIdentification(@JsonProperty(JSON_VALUE) String value) {
        super(FireAsPersonIdenticationType.PARTNER_USER_ID, value);
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

        public PartnerUserIdFireAsPersonIdentification build() {
            return new PartnerUserIdFireAsPersonIdentification(value);
        }
    }
}
