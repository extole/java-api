package com.extole.client.rest.person;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonBlockResponse {

    private static final String TYPE = "type";
    private static final String REASON = "reason";
    private static final String UPDATED_DATE = "updated_date";

    private final PersonBlockType type;
    private final Optional<String> reason;
    private final Optional<ZonedDateTime> updatedDate;

    public PersonBlockResponse(
        @JsonProperty(TYPE) PersonBlockType type,
        @JsonProperty(REASON) Optional<String> reason,
        @JsonProperty(UPDATED_DATE) Optional<ZonedDateTime> updatedDate) {
        this.type = type;
        this.reason = reason;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(TYPE)
    public PersonBlockType getType() {
        return type;
    }

    @JsonProperty(REASON)
    public Optional<String> getReason() {
        return reason;
    }

    @JsonProperty(UPDATED_DATE)
    public Optional<ZonedDateTime> getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
