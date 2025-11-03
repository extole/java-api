package com.extole.common.ip;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class Country {

    private static final String ISO_CODE = "iso_code";
    private static final String NAME = "name";

    private final String isoCode;
    private final String name;

    @JsonCreator
    public Country(@JsonProperty(ISO_CODE) String isoCode,
        @JsonProperty(NAME) String name) {
        this.isoCode = isoCode;
        this.name = name;
    }

    @JsonProperty(ISO_CODE)
    public String getIsoCode() {
        return isoCode;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
