package com.extole.reporting.rest.report.access;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportSharedTokenResponse {

    private static final String JSON_ACCESS_TOKEN = "access_token";
    private static final String JSON_EXPIRES_ID = "expires_in";
    private final String token;
    private final long expiresIn;

    @JsonCreator
    public ReportSharedTokenResponse(@JsonProperty(JSON_ACCESS_TOKEN) String token,
        @JsonProperty(JSON_EXPIRES_ID) long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    @JsonProperty(JSON_ACCESS_TOKEN)
    public String getToken() {
        return token;
    }

    @JsonProperty(JSON_EXPIRES_ID)
    public long getExpiresIn() {
        return expiresIn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String token;
        private long expiresIn;

        private Builder() {
        }

        public Builder withToken(String token) {
            this.token = token;
            return this;
        }

        public Builder withExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public ReportSharedTokenResponse build() {
            return new ReportSharedTokenResponse(token, expiresIn);
        }
    }
}
