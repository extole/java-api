package com.extole.consumer.rest.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlaceResponse {
    private static final String JSON_FORMATTED_ADDRESS = "formatted_address";

    private final String formattedAddress;

    public PlaceResponse(@JsonProperty(JSON_FORMATTED_ADDRESS) String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    @JsonProperty(JSON_FORMATTED_ADDRESS)
    public String getFormattedAddress() {
        return formattedAddress;
    }
}
