package com.extole.consumer.rest.validation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostalAddress {
    private static final String JSON_REGION_CODE = "region_code";
    private static final String JSON_LANGUAGE_CODE = "language_code";
    private static final String JSON_ADDRESS_LINES = "address_lines";

    private final String regionCode;
    private final String languageCode;
    private final List<String> addressLines;

    public PostalAddress(@JsonProperty(JSON_REGION_CODE) String regionCode,
        @JsonProperty(JSON_LANGUAGE_CODE) String languageCode,
        @JsonProperty(JSON_ADDRESS_LINES) List<String> addressLines) {
        this.regionCode = regionCode;
        this.languageCode = languageCode;
        this.addressLines = addressLines;
    }

    @JsonProperty(JSON_REGION_CODE)
    public String getRegionCode() {
        return regionCode;
    }

    @JsonProperty(JSON_LANGUAGE_CODE)
    public String getLanguageCode() {
        return languageCode;
    }

    @JsonProperty(JSON_ADDRESS_LINES)
    public List<String> getAddressLines() {
        return addressLines;
    }
}
