package com.extole.client.rest.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPasswordUpdateRequest {
    private static final String JSON_PASSWORD = "password";
    private final String password;

    public UserPasswordUpdateRequest(
        @JsonProperty(JSON_PASSWORD) String password) {
        this.password = password;
    }

    @JsonProperty(JSON_PASSWORD)
    public String getPassword() {
        return password;
    }
}
