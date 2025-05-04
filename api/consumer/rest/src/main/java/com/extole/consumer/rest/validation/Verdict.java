package com.extole.consumer.rest.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Verdict {
    private static final String JSON_INPUT_GRANULARITY = "input_granularity";
    private static final String JSON_VALIDATION_GRANULARITY = "validation_granularity";
    private static final String JSON_GEOCODE_GRANULARITY = "geocode_granularity";
    private static final String JSON_HAS_UNCONFIRMED_COMPONENTS = "has_unconfirmed_components";

    private final String inputGranularity;
    private final String validationGranularity;
    private final String geocodeGranularity;
    private final Boolean hasUnconfirmedComponents;

    public Verdict(
        @JsonProperty(JSON_INPUT_GRANULARITY) String inputGranularity,
        @JsonProperty(JSON_VALIDATION_GRANULARITY) String validationGranularity,
        @JsonProperty(JSON_GEOCODE_GRANULARITY) String geocodeGranularity,
        @JsonProperty(JSON_HAS_UNCONFIRMED_COMPONENTS) Boolean hasUnconfirmedComponents) {
        this.inputGranularity = inputGranularity;
        this.validationGranularity = validationGranularity;
        this.geocodeGranularity = geocodeGranularity;
        this.hasUnconfirmedComponents = hasUnconfirmedComponents;
    }

    @JsonProperty(JSON_INPUT_GRANULARITY)
    public String getInputGranularity() {
        return inputGranularity;
    }

    @JsonProperty(JSON_VALIDATION_GRANULARITY)
    public String getValidationGranularity() {
        return validationGranularity;
    }

    @JsonProperty(JSON_GEOCODE_GRANULARITY)
    public String getGeocodeGranularity() {
        return geocodeGranularity;
    }

    @JsonProperty(JSON_HAS_UNCONFIRMED_COMPONENTS)
    public Boolean getHasUnconfirmedComponents() {
        return hasUnconfirmedComponents;
    }
}
