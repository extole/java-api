package com.extole.client.rest.campaign.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PartnerEventIdFireAsPersonIdentification extends FireAsPersonIdentification {
    static final String TYPE = "PARTNER_EVENT_ID";

    private static final String JSON_PARTNER_EVENT_KEY = "partner_event_key";

    private final String partnerEventKey;

    @JsonCreator
    public PartnerEventIdFireAsPersonIdentification(@JsonProperty(JSON_PARTNER_EVENT_KEY) String partnerEventKey,
        @JsonProperty(JSON_VALUE) String value) {
        super(FireAsPersonIdenticationType.PARTNER_EVENT_ID, value);
        this.partnerEventKey = partnerEventKey;
    }

    @JsonProperty(JSON_PARTNER_EVENT_KEY)
    public String getPartnerEventKey() {
        return partnerEventKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String partnerEventKey;
        private String value;

        private Builder() {
        }

        public Builder withPartnerEventKey(String partnerEventKey) {
            this.partnerEventKey = partnerEventKey;
            return this;
        }

        public Builder withValue(String value) {
            this.value = value;
            return this;
        }

        public PartnerEventIdFireAsPersonIdentification build() {
            return new PartnerEventIdFireAsPersonIdentification(partnerEventKey, value);
        }
    }
}
