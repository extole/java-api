package com.extole.consumer.rest.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressValidationResponse {

    private static final String JSON_VERDICT = "verdict";
    private static final String JSON_ADDRESS = "address";

    private final Verdict verdict;
    private final Address address;

    public AddressValidationResponse(@JsonProperty(JSON_VERDICT) Verdict verdict,
        @JsonProperty(JSON_ADDRESS) Address address) {
        this.verdict = verdict;
        this.address = address;
    }

    @JsonProperty(JSON_VERDICT)
    public Verdict getVerdict() {
        return verdict;
    }

    @JsonProperty(JSON_ADDRESS)
    public Address getAddress() {
        return address;
    }

}
