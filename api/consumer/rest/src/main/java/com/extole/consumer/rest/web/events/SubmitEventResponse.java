package com.extole.consumer.rest.web.events;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SubmitEventResponse {

    private static final String ID = "id";
    private static final String TOKEN = "token";
    private static final String COOKIE_CONSENT = "cookie_consent";
    private final String id;
    private final String token;
    private final String cookieConsent;

    @JsonCreator
    public SubmitEventResponse(@JsonProperty(ID) String id,
        @JsonProperty(TOKEN) String token,
        @Nullable @JsonProperty(COOKIE_CONSENT) String cookieConsent) {
        this.id = id;
        this.token = token;
        this.cookieConsent = cookieConsent;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(TOKEN)
    public String getToken() {
        return token;
    }

    @Nullable
    @JsonProperty(COOKIE_CONSENT)
    public String getCookieConsent() {
        return cookieConsent;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String id;
        private String token;
        private String cookieConsent;

        private Builder() {

        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withToken(String token) {
            this.token = token;
            return this;
        }

        public Builder withCookieConsent(String cookieConsent) {
            this.cookieConsent = cookieConsent;
            return this;
        }

        public SubmitEventResponse build() {
            return new SubmitEventResponse(id, token, cookieConsent);
        }
    }
}
