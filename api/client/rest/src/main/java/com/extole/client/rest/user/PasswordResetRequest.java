package com.extole.client.rest.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordResetRequest {
    private static final String EMAIL = "email";
    private final String email;

    public PasswordResetRequest(
        @JsonProperty(EMAIL) String email) {
        this.email = email;
    }

    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }
}
