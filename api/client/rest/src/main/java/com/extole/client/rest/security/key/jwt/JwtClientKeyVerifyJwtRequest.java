package com.extole.client.rest.security.key.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class JwtClientKeyVerifyJwtRequest {

    private static final String JWT = "jwt";

    private final String jwt;

    public JwtClientKeyVerifyJwtRequest(@JsonProperty(JWT) String jwt) {
        this.jwt = jwt;
    }

    @JsonProperty(JWT)
    public String getJwt() {
        return jwt;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
