package com.extole.client.rest.person;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonBlockRequest {

    private static final String TYPE = "type";
    private static final String REASON = "reason";

    private final PersonBlockType type;
    private final Optional<String> reason;

    public PersonBlockRequest(
        @JsonProperty(TYPE) PersonBlockType type,
        @JsonProperty(REASON) Optional<String> reason) {
        this.type = type;
        this.reason = reason;
    }

    @JsonProperty(TYPE)
    public PersonBlockType getType() {
        return type;
    }

    @JsonProperty(REASON)
    public Optional<String> getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
