package com.extole.client.rest.subcription;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SubscriptionUserResponse {
    private static final String USER_ID = "user_id";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private final String userId;
    private final String email;
    private final String firstName;
    private final String lastName;

    @JsonCreator
    public SubscriptionUserResponse(
        @JsonProperty(USER_ID) String userId,
        @JsonProperty(EMAIL) String email,
        @Nullable @JsonProperty(FIRST_NAME) String firstName,
        @Nullable @JsonProperty(LAST_NAME) String lastName) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @JsonProperty(USER_ID)
    public String getUserId() {
        return userId;
    }

    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }

    @Nullable
    @JsonProperty(FIRST_NAME)
    public String getFirstName() {
        return firstName;
    }

    @Nullable
    @JsonProperty(LAST_NAME)
    public String getLastName() {
        return lastName;
    }
}
