package com.extole.consumer.rest.validation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressValidationRequest {
    private static final String JSON_ADDRESS_LINES = "address_lines";
    private static final String JSON_REGION_CODE = "region_code";

    private final List<String> addressLines;
    private final String regionCode;

    public AddressValidationRequest(
        @JsonProperty(JSON_REGION_CODE) String regionCode,
        @JsonProperty(JSON_ADDRESS_LINES) List<String> addressLines) {
        this.regionCode = regionCode;
        this.addressLines = addressLines;
    }

    @JsonProperty(JSON_ADDRESS_LINES)
    public List<String> getAddressLines() {
        return addressLines;
    }

    @JsonProperty(JSON_REGION_CODE)
    public String getRegionCode() {
        return regionCode;
    }
}
