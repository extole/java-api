package com.extole.client.rest.person;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonLocaleResponse {

    private static final String JSON_USER_SPECIFIED = "user_specified";
    private static final String JSON_LAST_BROWSER = "last_browser";

    private final Optional<String> lastBrowser;
    private final Optional<String> userSpecified;

    @JsonCreator
    public PersonLocaleResponse(
        @JsonProperty(JSON_LAST_BROWSER) Optional<String> lastBrowser,
        @JsonProperty(JSON_USER_SPECIFIED) Optional<String> userSpecified) {
        this.lastBrowser = lastBrowser;
        this.userSpecified = userSpecified;
    }

    @JsonProperty(JSON_LAST_BROWSER)
    public Optional<String> getLastBrowser() {
        return lastBrowser;
    }

    @JsonProperty(JSON_USER_SPECIFIED)
    public Optional<String> getUserSpecified() {
        return userSpecified;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
