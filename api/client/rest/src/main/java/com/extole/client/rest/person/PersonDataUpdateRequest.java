package com.extole.client.rest.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class PersonDataUpdateRequest {

    private static final String JSON_SCOPE = "scope";
    private static final String JSON_VALUE = "value";

    private final Omissible<PersonDataScope> scope;
    private final Omissible<Object> value;

    @JsonCreator
    public PersonDataUpdateRequest(
        @Parameter(
            description = "Scope of the data parameter to update.") @JsonProperty(JSON_SCOPE) Omissible<
                PersonDataScope> scope,
        @Parameter(
            description = "Value of the data parameter to update.") @JsonProperty(JSON_VALUE) Omissible<Object> value) {
        this.scope = scope;
        this.value = value;
    }

    @JsonProperty(JSON_SCOPE)
    public Omissible<PersonDataScope> getScope() {
        return scope;
    }

    @JsonProperty(JSON_VALUE)
    public Omissible<Object> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<PersonDataScope> scope = Omissible.omitted();
        private Omissible<Object> value = Omissible.omitted();

        private Builder() {
        }

        public Builder withScope(PersonDataScope scope) {
            this.scope = Omissible.of(scope);
            return this;
        }

        public Builder withValue(Object value) {
            this.value = Omissible.of(value);
            return this;
        }

        public PersonDataUpdateRequest build() {
            return new PersonDataUpdateRequest(scope, value);
        }
    }
}
