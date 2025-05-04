package com.extole.client.rest.person.relationship.v2;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonRelationshipV2UpdateRequest {

    private static final String JSON_CONTAINER = "container";

    private final String container;

    @JsonCreator
    public PersonRelationshipV2UpdateRequest(
        @Nullable @JsonProperty(JSON_CONTAINER) String container) {
        this.container = container;
    }

    @Nullable
    @JsonProperty(JSON_CONTAINER)
    public String getContainer() {
        return container;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String container;

        private Builder() {
        }

        public Builder withContainer(String container) {
            this.container = container;
            return this;
        }

        public PersonRelationshipV2UpdateRequest build() {
            return new PersonRelationshipV2UpdateRequest(container);
        }

    }

}
