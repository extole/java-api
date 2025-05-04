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

    public Optional<OAuthClientKeyBuildRestException> map(BuildClientKeyException e) {
        return internalMap(e);
    }

    private Optional<OAuthClientKeyBuildRestException> internalMap(BuildClientKeyException e) {
        OAuthClientKeyBuildRestException result = null;
        if (e instanceof InvalidAuthorizationUrlOAuthClientKeyException) {
            InvalidAuthorizationUrlOAuthClientKeyException ex = (InvalidAuthorizationUrlOAuthClientKeyException) e;
            result = RestExceptionBuilder.newBuilder(
                OAuthClientKeyBuildRestException.class)
                .withErrorCode(OAuthClientKeyBuildRestException.AUTHORIZATION_URL_INVALID)
                .addParameter("authorization_url", ex.getAuthorizationUrl())
                .withCause(e)
                .build();
        }
        if (e instanceof InvalidUriAuthorizationUrlOAuthClientKeyException) {
            InvalidUriAuthorizationUrlOAuthClientKeyException ex =
                (InvalidUriAuthorizationUrlOAuthClientKeyException) e;
            result = RestExceptionBuilder.newBuilder(
                OAuthClientKeyBuildRestException.class)
                .withErrorCode(
                    OAuthClientKeyBuildRestException.AUTHORIZATION_URL_INVALID_URI)
                .addParameter("authorization_url", ex.getAuthorizationUrl())
                .withCause(e)
                .build();
        }
        if (e instanceof MissingAuthorizationUrlOAuthClientKeyException) {
            MissingAuthorizationUrlOAuthClientKeyException ex = (MissingAuthorizationUrlOAuthClientKeyException) e;
            result = RestExceptionBuilder.newBuilder(
                OAuthClientKeyBuildRestException.class)
                .withErrorCode(
                    OAuthClientKeyBuildRestException.MISSING_AUTHORIZATION_URL)
                .withCause(ex)
                .build();
        }
        if (e instanceof TooLongAuthorizationUrlOAuthClientKeyException) {
            TooLongAuthorizationUrlOAuthClientKeyException ex = (TooLongAuthorizationUrlOAuthClientKeyException) e;
            result = RestExceptionBuilder.newBuilder(
                OAuthClientKeyBuildRestException.class)
                .withErrorCode(
                    OAuthClientKeyBuildRestException.AUTHORIZATION_URL_TOO_LONG)
                .addParameter("authorization_url", ex.getAuthorizationUrl())
                .addParameter("max_length", Integer.valueOf(ex.getMaxLength()))
                .withCause(e)
                .build();
        }

        return Optional.ofNullable(result);
    }
}
