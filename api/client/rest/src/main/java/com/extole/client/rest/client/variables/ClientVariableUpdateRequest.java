package com.extole.client.rest.client.variables;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class ClientVariableUpdateRequest {
    private static final String VALUE = "value";
    private static final String DESCRIPTION = "description";

    private final Omissible<Optional<String>> value;
    private final Omissible<Optional<String>> description;

    public ClientVariableUpdateRequest(@JsonProperty(VALUE) Omissible<Optional<String>> value,
        @JsonProperty(DESCRIPTION) Omissible<Optional<String>> description) {
        this.value = value;
        this.description = description;
    }

    @JsonProperty(VALUE)
    public Omissible<Optional<String>> getValue() {
        return value;
    }

    @JsonProperty(DESCRIPTION)
    public Omissible<Optional<String>> getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Optional<String>> value = Omissible.omitted();
        private Omissible<Optional<String>> description = Omissible.omitted();

        private Builder() {

        }

        public Builder withValue(String value) {
            this.value = Omissible.of(Optional.ofNullable(value));
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Omissible.of(Optional.ofNullable(description));
            return this;
        }

        public Builder clearDescription() {
            this.description = Omissible.of(Optional.empty());
            return this;
        }

        public ClientVariableUpdateRequest build() {
            return new ClientVariableUpdateRequest(value,
                description);
        }
    }
}
