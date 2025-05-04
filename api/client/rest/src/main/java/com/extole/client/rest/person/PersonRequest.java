package com.extole.client.rest.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

@Schema(description = "Extole PersonRequest")
public class PersonRequest {

    private static final String IDENTITY_KEY_VALUE = "identity_key_value";

    private final Omissible<String> identityKeyValue;

    public PersonRequest(
        @JsonProperty(IDENTITY_KEY_VALUE) Omissible<String> identityKeyValue) {
        this.identityKeyValue = identityKeyValue;
    }

    @JsonProperty(IDENTITY_KEY_VALUE)
    public Omissible<String> getIdentityKeyValue() {
        return identityKeyValue;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<String> identityKeyValue = Omissible.omitted();

        private Builder() {
        }

        public Builder withIdentityKeyValue(String identityKeyValue) {
            this.identityKeyValue = Omissible.of(identityKeyValue);
            return this;
        }

        public PersonRequest build() {
            return new PersonRequest(identityKeyValue);
        }
    }
}
