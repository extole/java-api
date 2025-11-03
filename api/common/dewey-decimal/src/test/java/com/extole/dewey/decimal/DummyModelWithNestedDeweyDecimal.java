package com.extole.dewey.decimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DummyModelWithNestedDeweyDecimal {

    private static final String PROPERTY_NAME_VALUE = "value";

    private final DeweyDecimal value;

    @JsonCreator
    public DummyModelWithNestedDeweyDecimal(@JsonProperty(PROPERTY_NAME_VALUE) DeweyDecimal value) {
        this.value = value;
    }

    @JsonProperty(PROPERTY_NAME_VALUE)
    public DeweyDecimal getValue() {
        return value;
    }

}
