package com.extole.client.rest.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class PersonDataCreateRequest {

    private static final String JSON_NAME = "name";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_VALUE = "value";

    private final String name;
    private final Omissible<PersonDataScope> scope;
    private final Object value;

    @JsonCreator
    public PersonDataCreateRequest(
        @Parameter(description = "Name of the data parameter to create.")
        @JsonProperty(JSON_NAME) String name,
        @Parameter(description = "Scope of the data parameter to create, defaults to PRIVATE when not provided.")
        @JsonProperty(JSON_SCOPE) Omissible<PersonDataScope> scope,
        @Parameter(description = "Value of the data parameter to create.")
        @JsonProperty(JSON_VALUE) Object value) {
        this.name = name;
        this.scope = scope;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_SCOPE)
    public Omissible<PersonDataScope> getScope() {
        return scope;
    }

    @JsonProperty(JSON_VALUE)
    public Object getValue() {
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
        private String name;
        private Omissible<PersonDataScope> scope = Omissible.omitted();
        private Object value;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withScope(PersonDataScope scope) {
            this.scope = Omissible.of(scope);
            return this;
        }

        public Builder withValue(Object value) {
            this.value = value;
            return this;
        }

        public PersonDataCreateRequest build() {
            return new PersonDataCreateRequest(name, scope, value);
        }
    }
}
