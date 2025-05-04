package com.extole.consumer.rest.email;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailProviderValidationResponse {
    private static final String EMAIL = "email";
    private static final String PROVIDER = "provider";
    private final String email;
    private final EmailProvider provider;

    @JsonCreator
    public EmailProviderValidationResponse(@JsonProperty(EMAIL) String email,
        @JsonProperty(PROVIDER) EmailProvider provider) {
        this.email = email;
        this.provider = provider;
    }

    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }

    @JsonProperty(PROVIDER)
    public EmailProvider getProvider() {
        return provider;
    }
}
