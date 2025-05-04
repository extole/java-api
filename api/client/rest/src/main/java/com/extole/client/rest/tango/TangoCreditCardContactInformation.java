package com.extole.client.rest.tango;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TangoCreditCardContactInformation {

    private static final String EMAIL_ADDRESS = "email_address";
    private static final String FULL_NAME = "full_name";

    private final String emailAddress;
    private final String fullName;

    public TangoCreditCardContactInformation(@JsonProperty(EMAIL_ADDRESS) String emailAddress,
        @JsonProperty(FULL_NAME) String fullName) {
        this.emailAddress = emailAddress;
        this.fullName = fullName;
    }

    @JsonProperty(EMAIL_ADDRESS)
    public String getEmailAddress() {
        return emailAddress;
    }

    @JsonProperty(FULL_NAME)
    public String getFullName() {
        return fullName;
    }

}
