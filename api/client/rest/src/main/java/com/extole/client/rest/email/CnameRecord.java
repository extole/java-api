package com.extole.client.rest.email;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CnameRecord {
    private static final String ALIAS = "alias";
    private static final String CANONICAL_NAME = "canonical_name";

    private final String alias;
    private final String canonicalName;

    @JsonCreator
    public CnameRecord(@JsonProperty(ALIAS) String alias, @JsonProperty(CANONICAL_NAME) String canonicalName) {
        this.alias = alias;
        this.canonicalName = canonicalName;
    }

    @JsonProperty(ALIAS)
    public String getAlias() {
        return alias;
    }

    @JsonProperty(CANONICAL_NAME)
    public String getCanonicalName() {
        return canonicalName;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
