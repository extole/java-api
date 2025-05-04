package com.extole.consumer.rest.authorization.v4;

import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.consumer.rest.common.Scope;

@JsonInclude(value = Include.NON_NULL)
public class TokenV4Request {
    private final Set<Scope> scopes;
    private final String jwt;
    private final String email;
    private final Long durationSeconds;

    @JsonCreator
    public TokenV4Request(@Nullable @JsonProperty("scopes") Set<Scope> scopes,
        @Deprecated // TBD - OPEN TICKET
        @Nullable @JsonProperty("duration") Long duration,
        @Nullable @JsonProperty("duration_seconds") Long durationSeconds,
        @Nullable @JsonProperty("jwt") String jwt,
        @Nullable @JsonProperty("email") String email) {
        this.scopes = scopes;
        this.jwt = jwt;
        this.email = email;
        if (durationSeconds != null) {
            this.durationSeconds = durationSeconds;
        } else {
            this.durationSeconds = duration;
        }
    }

    public TokenV4Request(Set<Scope> scopes, Long duration) {
        this(scopes, duration, null, null, null);
    }

    @Nullable
    @JsonProperty("scopes")
    public Set<Scope> getScopes() {
        return scopes;
    }

    @Nullable
    @JsonProperty("jwt")
    public String getJwt() {
        return jwt;
    }

    @Nullable
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @Nullable
    @JsonProperty("duration_seconds")
    public Long getDurationSeconds() {
        return durationSeconds;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
