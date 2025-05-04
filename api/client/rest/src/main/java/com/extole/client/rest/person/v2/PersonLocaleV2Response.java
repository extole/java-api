package com.extole.client.rest.person.v2;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonLocaleV2Response {

    private static final String JSON_USER_SPECIFIED = "user_specified";
    private static final String JSON_LAST_BROWSER = "last_browser";

    private final String lastBrowser;
    private final String userSpecified;

    @JsonCreator
    public PersonLocaleV2Response(
        @Nullable @JsonProperty(JSON_LAST_BROWSER) String lastBrowser,
        @Nullable @JsonProperty(JSON_USER_SPECIFIED) String userSpecified) {
        this.lastBrowser = lastBrowser;
        this.userSpecified = userSpecified;
    }

    @Nullable
    @JsonProperty(JSON_LAST_BROWSER)
    public String getLastBrowser() {
        return lastBrowser;
    }

    @Nullable
    @JsonProperty(JSON_USER_SPECIFIED)
    public String getUserSpecified() {
        return userSpecified;
    }

}
