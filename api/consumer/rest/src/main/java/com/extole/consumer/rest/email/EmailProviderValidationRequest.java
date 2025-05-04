package com.extole.consumer.rest.email;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailProviderValidationRequest {
    private static final String EMAIL = "email";
    private final String email;

    @JsonCreator
    public EmailProviderValidationRequest(@JsonProperty(EMAIL) String email) {
        this.email = email;
    }

    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }
}
