package com.extole.client.rest.impl.client;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.authorization.service.client.managed.ManagedAuthorization;
import com.extole.authorization.service.client.managed.ManagedAuthorizationBuilder;
import com.extole.authorization.service.client.managed.ManagedAuthorizationNegativeDurationException;
import com.extole.authorization.service.client.managed.ManagedAuthorizationService;
import com.extole.client.rest.client.AccessTokenCreationRestException;
import com.extole.client.rest.client.OAuthAccessTokenResponse;
import com.extole.client.rest.client.OAuthClientCredentialsAccessTokenEndpoints;
import com.extole.client.rest.client.OAuthClientCredentialsAccessTokenRestException;
import com.extole.client.rest.client.OAuthClientCredentialsRequestParameters;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;

@Provider
public class OAuthClientCredentialsAccessTokenEndpointsImpl implements OAuthClientCredentialsAccessTokenEndpoints {

    private static final Duration DEFAULT_TIME_TO_LIVE_DURATION = Duration.ofSeconds(3600);

    private static final String BEARER = "Bearer";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "clientSecret";
    private static final Pattern CREDENTIALS_PATTERN =
        Pattern.compile("^(?<" + CLIENT_ID + ">[^:]+):(?<" + CLIENT_SECRET + ">[^:]+)$");

    private final ClientAuthorizationProvider authorizationProvider;
    private final ManagedAuthorizationService authorizationService;

    @Autowired
    public OAuthClientCredentialsAccessTokenEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ManagedAuthorizationService authorizationService) {
        this.authorizationProvider = authorizationProvider;
        this.authorizationService = authorizationService;
    }

    @Override
    public OAuthAccessTokenResponse create(String credentials,
        OAuthClientCredentialsRequestParameters requestParameters)
        throws OAuthClientCredentialsAccessTokenRestException, UserAuthorizationRestException,
        AccessTokenCreationRestException {

        ClientAuthorization authorization;
        if (requestParameters.getClientSecret().isPresent()) {
            authorization = authorizationProvider.getClientAuthorization(requestParameters.getClientSecret().get());
        } else if (!Strings.isNullOrEmpty(credentials)) {
            authorization = authorizationProvider.getClientAuthorization(getCredentialsAccessToken(credentials));
        } else {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_TOKEN_MISSING)
                .build();
        }
        validateAccess(authorization);
        validateGrantType(requestParameters.getGrantType());

        try {
            ManagedAuthorizationBuilder authorizationBuilder = authorizationService.create(authorization)
                .withDuration(DEFAULT_TIME_TO_LIVE_DURATION)
                .withScopes(authorization.getScopes());
            return toResponse(authorizationBuilder.build());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ManagedAuthorizationNegativeDurationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private static OAuthAccessTokenResponse toResponse(ClientAuthorization authorization) {
        return new OAuthAccessTokenResponse(authorization.getAccessToken(), BEARER,
            Long.valueOf(authorization.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond()));
    }

    private static String getCredentialsAccessToken(String credentials) throws UserAuthorizationRestException {
        try {
            String decodedCredentials = new String(Base64.getDecoder().decode(credentials));
            Matcher matcher = CREDENTIALS_PATTERN.matcher(decodedCredentials);
            if (matcher.find()) {
                return matcher.group(CLIENT_SECRET);
            } else {
                throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                    .withErrorCode(UserAuthorizationRestException.ACCESS_TOKEN_MISSING)
                    .build();
            }
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_TOKEN_MISSING)
                .build();
        }
    }

    private static void validateAccess(Authorization authorization) throws UserAuthorizationRestException {
        if (!(authorization instanceof ManagedAuthorization) ||
            !authorization.isAuthorized(authorization.getClientId(), Authorization.Scope.CLIENT_ADMIN)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
    }

    private static void validateGrantType(String grantType) throws OAuthClientCredentialsAccessTokenRestException {
        if (!GRANT_TYPE.equalsIgnoreCase(grantType)) {
            throw RestExceptionBuilder.newBuilder(OAuthClientCredentialsAccessTokenRestException.class)
                .withErrorCode(OAuthClientCredentialsAccessTokenRestException.UNSUPPORTED_GRANT_TYPE)
                .addParameter("grant_type", grantType)
                .build();
        }
    }

}
