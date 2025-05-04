package com.extole.api.client.security.key;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface OAuthClientCredentials {

    String getAuthorizationUrl();

    String getOAuthClientId();

    String getOAuthClientSecret();

    @Nullable
    String getScope();

}
