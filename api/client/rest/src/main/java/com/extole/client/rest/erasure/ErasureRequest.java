package com.extole.client.rest.erasure;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class ErasureRequest {
    private static final String JSON_PROPERTY_EMAIL = "email";
    private static final String JSON_PROPERTY_PARTNER_USER_ID = "partner_user_id";
    private static final String JSON_PROPERTY_NOTE = "note";

    private final Omissible<String> email;
    private final Omissible<String> partnerUserId;
    private final Omissible<String> note;

    public ErasureRequest(
        @JsonProperty(JSON_PROPERTY_EMAIL) Omissible<String> email,
        @JsonProperty(JSON_PROPERTY_PARTNER_USER_ID) Omissible<String> partnerUserId,
        @JsonProperty(value = JSON_PROPERTY_NOTE) Omissible<String> note) {
        this.email = email;
        this.partnerUserId = partnerUserId;
        this.note = note;
    }

    @JsonProperty(JSON_PROPERTY_EMAIL)
    public Omissible<String> getEmail() {
        return email;
    }

    @JsonProperty(JSON_PROPERTY_PARTNER_USER_ID)
    public Omissible<String> getPartnerUserId() {
        return partnerUserId;
    }

    @JsonProperty(JSON_PROPERTY_NOTE)
    public Omissible<String> getNote() {
        return note;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<String> email = Omissible.omitted();
        private Omissible<String> partnerUserId = Omissible.omitted();
        private Omissible<String> note = Omissible.omitted();

        private Builder() {
        }

        public Builder withEmail(String email) {
            this.email = Omissible.of(email);
            return this;
        }

        public Builder withPartnerUserId(String partnerUserId) {
            this.partnerUserId = Omissible.of(partnerUserId);
            return this;
        }

        public Builder withNote(String note) {
            this.note = Omissible.of(note);
            return this;
        }

        public ErasureRequest build() {
            return new ErasureRequest(email, partnerUserId, note);
        }
    }
}
