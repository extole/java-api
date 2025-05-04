package com.extole.consumer.rest.authorization;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

@JsonInclude(value = Include.NON_NULL)
public class CreateTokenRequest {
    private static final String DURATION_SECONDS = "duration_seconds";
    private static final String JWT = "jwt";
    private static final String EMAIL = "email";
    private final String jwt;
    private final String email;
    private final Long durationSeconds;

    @JsonCreator
    public CreateTokenRequest(
        @Nullable @JsonProperty(DURATION_SECONDS) Long durationSeconds,
        @Nullable @JsonProperty(JWT) String jwt,
        @Nullable @JsonProperty(EMAIL) String email) {
        this.jwt = jwt;
        this.email = email;
        this.durationSeconds = durationSeconds;
    }

    @Nullable
    @JsonProperty(JWT)
    public String getJwt() {
        return jwt;
    }

    @Nullable
    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }

    @Nullable
    @JsonProperty(DURATION_SECONDS)
    public Long getDurationSeconds() {
        return durationSeconds;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
