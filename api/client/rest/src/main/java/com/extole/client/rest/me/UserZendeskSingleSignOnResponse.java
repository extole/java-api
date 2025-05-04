package com.extole.client.rest.me;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserZendeskSingleSignOnResponse {
    private static final String URL = "url";
    private final String url;

    @JsonCreator
    public UserZendeskSingleSignOnResponse(@JsonProperty(URL) String url) {
        this.url = url;
    }

    @JsonProperty(URL)
    public String getUrl() {
        return url;
    }

}
