package com.extole.client.topic.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class DataValue {
    private static final String VALUE = "value";
    private static final String SCOPE = "scope";
    private static final String TYPE = "type";

    public enum Type {
        STRING, ATTACHMENT
    }

    private final String value;
    private final Type type;
    private final Scope scope;

    @JsonCreator
    public DataValue(@JsonProperty(VALUE) String value,
        @JsonProperty(TYPE) Type type,
        @JsonProperty(SCOPE) Scope scope) {
        this.value = value;
        this.type = type;
        this.scope = scope;
    }

    @JsonProperty(VALUE)
    public String getValue() {
        return value;
    }

    @JsonProperty(TYPE)
    public Type getType() {
        return type;
    }

    @JsonProperty(SCOPE)
    public Scope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
