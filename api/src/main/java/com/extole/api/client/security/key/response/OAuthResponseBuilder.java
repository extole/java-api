package com.extole.api.client.security.key.response;

public interface OAuthResponseBuilder {

    Long DEFAULT_EXPIRES_IN = Long.valueOf(86400);

    OAuthResponseBuilder withAccessToken(String accessToken);

    OAuthResponseBuilder withExpiresIn(Long expiresIn);

    OAuthResponse build();

}
