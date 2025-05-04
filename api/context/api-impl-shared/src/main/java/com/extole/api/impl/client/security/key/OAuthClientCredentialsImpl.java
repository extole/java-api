package com.extole.api.impl.client.security.key;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nullable;

import com.extole.api.client.security.key.OAuthClientCredentials;
import com.extole.model.entity.client.security.key.built.BuiltOAuthClientKey;
import com.extole.model.service.encryption.EncryptionService;

public class OAuthClientCredentialsImpl implements OAuthClientCredentials {

    private final String authorizationUrl;
    private final String oAuthClientId;
    private final String oAuthClientSecret;
    private final String scope;

    public OAuthClientCredentialsImpl(EncryptionService<byte[]> encryptionService,
        BuiltOAuthClientKey clientKey) {
        this.authorizationUrl = clientKey.getAuthorizationUrl().toString();
        this.oAuthClientId = clientKey.getOAuthClientId();
        this.oAuthClientSecret =
            new String(encryptionService.decrypt(clientKey.getEncryptedKey()), StandardCharsets.ISO_8859_1);
        this.scope = clientKey.getScope().orElse(null);
    }

    @Override
    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    @Override
    public String getOAuthClientId() {
        return oAuthClientId;
    }

    @Override
    public String getOAuthClientSecret() {
        return oAuthClientSecret;
    }

    @Override
    @Nullable
    public String getScope() {
        return scope;
    }
}
