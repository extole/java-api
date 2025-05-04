package com.extole.client.rest.person.v2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.extole.common.lang.ToString;

public final class PartnerEventIdV2Response {

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";

    private final String name;
    private final String value;

    @JsonCreator
    public PartnerEventIdV2Response(@JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_VALUE) String value) {
        this.name = name;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null) {
            return false;
        }

        if (this.getClass() != otherObject.getClass()) {
            return false;
        }

        PartnerEventIdV2Response otherPartnerEventId = (PartnerEventIdV2Response) otherObject;

        return EqualsBuilder.reflectionEquals(this, otherPartnerEventId);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
