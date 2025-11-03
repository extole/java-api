package com.extole.client.rest.impl.campaign;

import java.util.Optional;

import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.client.security.key.built.InvalidAuthorizationUrlOAuthClientKeyException;
import com.extole.model.service.client.security.key.built.InvalidUriAuthorizationUrlOAuthClientKeyException;
import com.extole.model.service.client.security.key.built.MissingAuthorizationUrlOAuthClientKeyException;
import com.extole.model.service.client.security.key.built.TooLongAuthorizationUrlOAuthClientKeyException;

public final class OAuthClientKeyBuildRestExceptionMapper {
    private static final OAuthClientKeyBuildRestExceptionMapper INSTANCE = new OAuthClientKeyBuildRestExceptionMapper();

    public static OAuthClientKeyBuildRestExceptionMapper getInstance() {
        return INSTANCE;
    }

    private OAuthClientKeyBuildRestExceptionMapper() {
    }

    public Optional<OAuthClientKeyBuildRestException> map(BuildClientKeyException exception) {
        return internalMap(exception);
    }

    private Optional<OAuthClientKeyBuildRestException> internalMap(BuildClientKeyException exception) {
        OAuthClientKeyBuildRestException result = null;
        if (exception instanceof InvalidAuthorizationUrlOAuthClientKeyException castedException) {
            result = RestExceptionBuilder.newBuilder(
                OAuthClientKeyBuildRestException.class)
                .withErrorCode(OAuthClientKeyBuildRestException.AUTHORIZATION_URL_INVALID)
                .addParameter("authorization_url", castedException.getAuthorizationUrl())
                .withCause(exception)
                .build();
        }
        if (exception instanceof InvalidUriAuthorizationUrlOAuthClientKeyException castedException) {
            result = RestExceptionBuilder.newBuilder(
                OAuthClientKeyBuildRestException.class)
                .withErrorCode(
                    OAuthClientKeyBuildRestException.AUTHORIZATION_URL_INVALID_URI)
                .addParameter("authorization_url", castedException.getAuthorizationUrl())
                .withCause(exception)
                .build();
        }
        if (exception instanceof MissingAuthorizationUrlOAuthClientKeyException castedException) {
            result = RestExceptionBuilder.newBuilder(
                OAuthClientKeyBuildRestException.class)
                .withErrorCode(
                    OAuthClientKeyBuildRestException.MISSING_AUTHORIZATION_URL)
                .withCause(castedException)
                .build();
        }
        if (exception instanceof TooLongAuthorizationUrlOAuthClientKeyException castedException) {
            result = RestExceptionBuilder.newBuilder(
                OAuthClientKeyBuildRestException.class)
                .withErrorCode(
                    OAuthClientKeyBuildRestException.AUTHORIZATION_URL_TOO_LONG)
                .addParameter("authorization_url", castedException.getAuthorizationUrl())
                .addParameter("max_length", Integer.valueOf(castedException.getMaxLength()))
                .withCause(exception)
                .build();
        }

        return Optional.ofNullable(result);
    }
}
