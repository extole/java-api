package com.extole.client.rest.impl.client;

import static com.extole.authorization.service.Authorization.Scope.CLIENT_ADMIN;
import static com.extole.authorization.service.Authorization.Scope.CLIENT_SUPERUSER;
import static com.extole.authorization.service.Authorization.Scope.USER_SUPPORT;
import static com.extole.client.rest.client.AccessTokenAuthCodeResponseValidateRestException.AUTHORIZATION_CODE_MISSING_CODE;
import static com.extole.client.rest.client.AccessTokenAuthCodeResponseValidateRestException.AUTHORIZATION_CODE_MISSING_CSRF;
import static com.extole.client.rest.client.AccessTokenAuthCodeResponseValidateRestException.AUTHORIZATION_CODE_MISSING_NONCE;
import static com.extole.client.rest.client.AccessTokenAuthCodeResponseValidateRestException.AUTHORIZATION_CODE_MISSING_STATE;
import static com.extole.client.rest.client.AccessTokenAuthCodeResponseValidateRestException.AUTHORIZATION_CODE_RESPONSE_INVALID;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.AuthorizationScopesDeniedException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.Identity;
import com.extole.authorization.service.InvalidExpiresAtException;
import com.extole.authorization.service.client.AuthorizationNotFoundException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.authorization.service.client.ClientAuthorizationService;
import com.extole.authorization.service.client.managed.ManagedAuthorization;
import com.extole.authorization.service.client.user.UserAuthorization;
import com.extole.authorization.service.client.user.UserAuthorizationBuilder;
import com.extole.authorization.service.client.user.UserAuthorizationService;
import com.extole.authorization.service.resource.ResourceAuthorization;
import com.extole.client.rest.client.AccessTokenAuthCodeResponseValidateRestException;
import com.extole.client.rest.client.AccessTokenCreationRequest;
import com.extole.client.rest.client.AccessTokenCreationRestException;
import com.extole.client.rest.client.AccessTokenResponse;
import com.extole.client.rest.client.ClientAccessTokenEndpoints;
import com.extole.client.rest.client.ClientAccessTokenRestException;
import com.extole.client.rest.client.Scope;
import com.extole.client.rest.client.core.AuthCodeResponseValidateRequest;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.id.Id;
import com.extole.model.entity.authorization.AuthorizablePrincipal;
import com.extole.model.entity.client.Client;
import com.extole.model.entity.user.User;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeBadCredentialsException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeCredentialsService;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeDisabledCredentialsException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeExpiredCredentialsException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeLockedCredentialsException;
import com.extole.model.service.client.ClientService;
import com.extole.model.service.client.security.ClientSecuritySettingsService;
import com.extole.model.service.user.UserNotFoundException;
import com.extole.model.service.user.UserService;
import com.extole.openid.connect.service.AuthCodeFlowErrorCode;
import com.extole.openid.connect.service.InvalidAuthCodeAuthResponseException;
import com.extole.openid.connect.service.OpenIdConnectService;
import com.extole.security.backend.BackendAuthorization;
import com.extole.security.backend.BackendAuthorizationProvider;

@Provider
public class ClientAccessTokenEndpointsImpl implements ClientAccessTokenEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(ClientAccessTokenEndpointsImpl.class);

    private static final String OIDC_AUTH_ERROR_CODE_PREFIX = "OIDC_AUTH_CODE_";
    private static final String DEFAULT_IMPERSONATE_USER = "support@extole.com";

    private final VerifiedEmailService verifiedEmailService;
    private final ExtoleAuthProviderTypeCredentialsService extoleAuthProviderTypeCredentialsService;
    private final UserAuthorizationService userAuthorizationService;
    private final ClientAuthorizationService clientAuthorizationService;
    private final UserService userService;
    private final ClientService clientService;
    private final ClientSecuritySettingsService clientSecuritySettingsService;
    private final OpenIdConnectService openIdConnectService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final BackendAuthorizationProvider backendAuthorizationProvider;

    @Autowired
    public ClientAccessTokenEndpointsImpl(
        VerifiedEmailService verifiedEmailService,
        ExtoleAuthProviderTypeCredentialsService extoleAuthProviderTypeCredentialsService,
        UserAuthorizationService userAuthorizationService,
        ClientAuthorizationService clientAuthorizationService,
        UserService userService,
        ClientService clientService,
        ClientSecuritySettingsService clientSecuritySettingsService,
        OpenIdConnectService openIdConnectService,
        ClientAuthorizationProvider authorizationProvider,
        BackendAuthorizationProvider backendAuthorizationProvider) {
        this.verifiedEmailService = verifiedEmailService;
        this.userAuthorizationService = userAuthorizationService;
        this.extoleAuthProviderTypeCredentialsService = extoleAuthProviderTypeCredentialsService;
        this.clientAuthorizationService = clientAuthorizationService;
        this.userService = userService;
        this.clientService = clientService;
        this.clientSecuritySettingsService = clientSecuritySettingsService;
        this.openIdConnectService = openIdConnectService;
        this.authorizationProvider = authorizationProvider;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
    }

    @Override
    public AccessTokenResponse create(String accessToken, Optional<AccessTokenCreationRequest> creationRequest)
        throws AccessTokenCreationRestException, UserAuthorizationRestException {
        AccessTokenCreationRequest request = creationRequest.orElse(AccessTokenCreationRequest.builder().build());
        ClientAuthorization authorization;
        try {
            if (!Strings.isNullOrEmpty(request.getEmail()) && !Strings.isNullOrEmpty(request.getPassword())) {
                String normalizedEmail =
                    verifiedEmailService.verifyEmail(request.getEmail()).getEmail().getNormalizedAddress();
                AuthorizablePrincipal authorizablePrincipal =
                    extoleAuthProviderTypeCredentialsService.authenticate(normalizedEmail, request.getPassword());
                authorization = userAuthorizationService.authorizeUserForClient(
                    backendAuthorizationProvider.getSuperuserAuthorizationForBackend(),
                    authorizablePrincipal.getClientId(), authorizablePrincipal.getPrincipal(),
                    authorizablePrincipal.getScopes())
                    .save();
            } else if (!Strings.isNullOrEmpty(accessToken)) {
                authorization = authorizationProvider.getClientAuthorization(accessToken);
            } else {
                throw RestExceptionBuilder.newBuilder(AccessTokenCreationRestException.class)
                    .withErrorCode(AccessTokenCreationRestException.CREDENTIALS_MISSING)
                    .build();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withCause(e)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        } catch (ExtoleAuthProviderTypeBadCredentialsException | InvalidEmailAddress | InvalidEmailDomainException e) {
            throw RestExceptionBuilder.newBuilder(AccessTokenCreationRestException.class)
                .withCause(e)
                .withErrorCode(AccessTokenCreationRestException.CREDENTIALS_INVALID)
                .build();
        } catch (ExtoleAuthProviderTypeLockedCredentialsException e) {
            throw RestExceptionBuilder.newBuilder(AccessTokenCreationRestException.class)
                .withCause(e)
                .withErrorCode(AccessTokenCreationRestException.ACCOUNT_LOCKED)
                .build();
        } catch (ExtoleAuthProviderTypeDisabledCredentialsException e) {
            throw RestExceptionBuilder.newBuilder(AccessTokenCreationRestException.class)
                .withCause(e)
                .withErrorCode(AccessTokenCreationRestException.ACCOUNT_DISABLED)
                .build();
        } catch (ExtoleAuthProviderTypeExpiredCredentialsException e) {
            throw RestExceptionBuilder.newBuilder(AccessTokenCreationRestException.class)
                .withCause(e)
                .withErrorCode(AccessTokenCreationRestException.CREDENTIALS_EXPIRED)
                .build();
        }

        Id<ClientHandle> clientId;
        Pair<Duration, Optional<Duration>> defaultDurationAndDuration;
        try {
            clientId = computeClientId(request.getClientId(), accessToken, authorization);
            defaultDurationAndDuration = getDefaultDurationAndDuration(clientId, request.getDurationSeconds());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AccessTokenCreationRestException.class)
                .withErrorCode(AccessTokenCreationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }

        if (authorization.getIdentity().getType() == Identity.Type.MANAGED) {
            UserAuthorization newAuthorization =
                impersonateManagedAuthorization((ManagedAuthorization) authorization, clientId);
            return mapToAccessTokenResponse(newAuthorization);
        }

        try {
            User user = userService.get(authorization);

            if (!authorization.getClientId().equals(clientId)
                && !authorization.getScopes().contains(CLIENT_SUPERUSER)) {
                BackendAuthorization backendAuthorization =
                    backendAuthorizationProvider.getAuthorizationForBackend(clientId);
                user = userService.getByNormalizedEmail(backendAuthorization, user.getNormalizedEmail());
                authorization = userAuthorizationService
                    .authorizeUserForClient(backendAuthorization, clientId, user, user.getAllowedScopes()).save();
            }

            UserAuthorizationBuilder authorizationBuilder =
                userAuthorizationService.authorizeUserForClient(authorization,
                    clientId, user, computeScopes(authorization, request));
            authorizationBuilder.withRefreshable(request.getDurationSeconds() == null);
            authorizationBuilder.withExpiresAt(
                Instant.now().plus(defaultDurationAndDuration.getRight().orElse(defaultDurationAndDuration.getLeft())));

            UserAuthorization clientAuthorization = authorizationBuilder.save();
            userService.updateLastUserLoginTime(clientAuthorization, userService.get(clientAuthorization));
            return mapToAccessTokenResponse(clientAuthorization);
        } catch (AuthorizationScopesDeniedException e) {
            throw RestExceptionBuilder.newBuilder(AccessTokenCreationRestException.class)
                .withErrorCode(AccessTokenCreationRestException.SCOPES_DENIED)
                .addParameter("denied_scopes", e.getDeniedScopes())
                .withCause(e)
                .build();
        } catch (UserNotFoundException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AccessTokenCreationRestException.class)
                .withErrorCode(AccessTokenCreationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (InvalidExpiresAtException e) {
            throw RestExceptionBuilder.newBuilder(AccessTokenCreationRestException.class)
                .withErrorCode(AccessTokenCreationRestException.INVALID_DURATION)
                .addParameter("duration_seconds", defaultDurationAndDuration.getRight().orElse(null))
                .addParameter("default_duration", defaultDurationAndDuration.getLeft())
                .withCause(e)
                .build();
        }
    }

    private UserAuthorization impersonateManagedAuthorization(ManagedAuthorization authorization,
        Id<ClientHandle> clientId) throws UserAuthorizationRestException {
        try {
            return userAuthorizationService
                .authorizeUserForClient(authorization, clientId, DEFAULT_IMPERSONATE_USER, authorization.getScopes())
                .withExpiresAt(Instant.now().plus(Duration.ofHours(BigDecimal.ONE.longValue())))
                .withRefreshable(false)
                .save();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (InvalidExpiresAtException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AccessTokenResponse validate(AuthCodeResponseValidateRequest authCodeResponseValidateRequest,
        String csrfToken, String nonce)
        throws AccessTokenAuthCodeResponseValidateRestException, AccessTokenCreationRestException {

        String code = authCodeResponseValidateRequest.getCode();
        if (code == null) {
            throw RestExceptionBuilder.newBuilder(AccessTokenAuthCodeResponseValidateRestException.class)
                .withErrorCode(AUTHORIZATION_CODE_MISSING_CODE)
                .build();
        }
        String state = authCodeResponseValidateRequest.getState();
        if (state == null) {
            throw RestExceptionBuilder.newBuilder(AccessTokenAuthCodeResponseValidateRestException.class)
                .withErrorCode(AUTHORIZATION_CODE_MISSING_STATE)
                .build();
        }
        if (csrfToken == null) {
            throw RestExceptionBuilder.newBuilder(AccessTokenAuthCodeResponseValidateRestException.class)
                .withErrorCode(AUTHORIZATION_CODE_MISSING_CSRF)
                .build();
        }
        if (nonce == null) {
            throw RestExceptionBuilder.newBuilder(AccessTokenAuthCodeResponseValidateRestException.class)
                .withErrorCode(AUTHORIZATION_CODE_MISSING_NONCE)
                .build();
        }
        AuthorizablePrincipal authorizablePrincipal;
        try {
            authorizablePrincipal = openIdConnectService.createAuthCodeValidationBuilder()
                .withCode(code)
                .withState(state)
                .withCsrfToken(csrfToken)
                .withNonce(nonce)
                .validate();
        } catch (InvalidAuthCodeAuthResponseException e) {
            LOG.warn("Authorization via authorization code flow failed.", e);
            throw RestExceptionBuilder.newBuilder(AccessTokenAuthCodeResponseValidateRestException.class)
                .withCause(e)
                .withErrorCode(AUTHORIZATION_CODE_RESPONSE_INVALID)
                .addParameter("error_code", getErrorCode(e.getError()))
                .build();
        }

        try {
            UserAuthorization authorization = userAuthorizationService.authorizeUserForClient(
                backendAuthorizationProvider.getSuperuserAuthorizationForBackend(), authorizablePrincipal.getClientId(),
                authorizablePrincipal.getPrincipal(), authorizablePrincipal.getScopes()).save();
            User user = userService.getById(authorization, authorization.getIdentityId());
            userService.updateLastUserLoginTime(authorization, user);
            return mapToAccessTokenResponse(authorization);
        } catch (UserNotFoundException e) {
            LOG.warn("Authorization via authorization code flow failed.", e);
            throw RestExceptionBuilder.newBuilder(AccessTokenAuthCodeResponseValidateRestException.class)
                .withCause(e)
                .withErrorCode(AUTHORIZATION_CODE_RESPONSE_INVALID)
                .addParameter("error_code", getErrorCode(AuthCodeFlowErrorCode.USER_NOT_FOUND))
                .build();
        } catch (AuthorizationScopesDeniedException e) {
            throw RestExceptionBuilder.newBuilder(AccessTokenCreationRestException.class)
                .withErrorCode(AccessTokenCreationRestException.SCOPES_DENIED)
                .addParameter("denied_scopes", e.getDeniedScopes())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public void delete(String accessToken) throws UserAuthorizationRestException {
        try {
            UserAuthorization resultAuthorization = userAuthorizationService.getByAccessToken(accessToken);
            userAuthorizationService.invalidate(resultAuthorization);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AccessTokenResponse getToken(String accessToken) throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        return mapToAccessTokenResponse(authorization);
    }

    @Override
    public AccessTokenResponse getToken(String authorizingAccessToken, String accessToken)
        throws UserAuthorizationRestException, ClientAccessTokenRestException {
        UserAuthorization authorizingAuthorization =
            authorizationProvider.getUserAuthorization(authorizingAccessToken);
        try {
            ClientAuthorization authorization =
                clientAuthorizationService.getClientAuthorization(authorizingAuthorization, accessToken);
            return mapToAccessTokenResponse(authorization);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AuthorizationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientAccessTokenRestException.class)
                .withErrorCode(ClientAccessTokenRestException.NOT_FOUND)
                .withCause(e)
                .build();
        }
    }

    private AccessTokenResponse mapToAccessTokenResponse(ClientAuthorization authorization) {
        long expiresAt = authorization.getExpiresAt().getEpochSecond();
        long expiresInSeconds = expiresAt - Instant.now().getEpochSecond();

        Set<Scope> scopes = authorization.getScopes()
            .stream()
            .map(Enum::name)
            .map(Scope::valueOf)
            .collect(Collectors.toSet());

        return AccessTokenResponse.builder()
            .withType(getType(authorization))
            .withToken(authorization.getAccessToken())
            .withClientId(authorization.getClientId().getValue())
            .withIdentityId(authorization.getIdentityId().getValue())
            .withExpiresIn(expiresInSeconds)
            .withScopes(scopes)
            .build();
    }

    @Override
    public AccessTokenResponse get(String accessToken) throws UserAuthorizationRestException {
        return this.getToken(accessToken);
    }

    @Override
    public AccessTokenResponse exchange(String accessToken) throws UserAuthorizationRestException {
        try {
            UserAuthorization newAuthorization = userAuthorizationService.exchangeOneTimeToken(accessToken);
            return mapToAccessTokenResponse(newAuthorization);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private Id<ClientHandle> computeClientId(@Nullable String requestedClientId, String accessToken,
        ClientAuthorization authorization) throws AuthorizationException {
        if (!Strings.isNullOrEmpty(requestedClientId)) {
            Id<ClientHandle> clientId = Id.valueOf(requestedClientId);

            if (Client.EXTOLE_CLIENT_ID.equals(clientId) && !authorization.getScopes().contains(CLIENT_SUPERUSER)) {
                throw new AuthorizationException("Extole client id: " + Client.EXTOLE_CLIENT_ID +
                    " passed when trying to create a client api access token without sufficient rights." +
                    " access token: " + accessToken);
            }

            boolean hasVisibility =
                clientService.getAll(authorization).stream().map(client -> client.getId())
                    .anyMatch(clientIdCandidate -> clientId.equals(clientIdCandidate));
            if (hasVisibility) {
                return clientId;
            } else {
                throw new AuthorizationException("Invalid client id: " + clientId +
                    " passed when trying to create a client api access token. authorization client:"
                    + authorization.getClientId());
            }
        }
        return authorization.getClientId();
    }

    private Set<Authorization.Scope> computeScopes(Authorization authorization, AccessTokenCreationRequest request) {
        Set<Authorization.Scope> resultScopes;
        if (request.getScopes().isEmpty()) {
            resultScopes = authorization.getScopes();
        } else {
            Set<Authorization.Scope> requestedScopes = request.getScopes().stream()
                .map(scope -> Authorization.Scope.valueOf(scope.name()))
                .collect(Collectors.toSet());
            ImmutableSet.Builder<Authorization.Scope> resultScopesBuilder = ImmutableSet.builder();
            resultScopesBuilder.addAll(requestedScopes);
            if (requestedScopes.contains(CLIENT_ADMIN)) {
                resultScopesBuilder.add(USER_SUPPORT);
            }
            resultScopes = resultScopesBuilder.build();
        }
        return authorization.getScopes().contains(CLIENT_SUPERUSER)
            ? SetUtils.union(resultScopes, Collections.singleton(CLIENT_SUPERUSER))
            : resultScopes;
    }

    private Pair<Duration, Optional<Duration>> getDefaultDurationAndDuration(Id<ClientHandle> clientId,
        @Nullable Long durationSeconds)
        throws AuthorizationException, AccessTokenCreationRestException {
        Duration defaultDuration = clientSecuritySettingsService
            .getSettings(backendAuthorizationProvider.getSuperuserAuthorizationForBackend(), clientId)
            .getClientTokenLifetime();

        if (durationSeconds != null) {
            Duration duration = Duration.ofSeconds(durationSeconds.longValue());
            if (defaultDuration.compareTo(duration) < 0) {
                throw RestExceptionBuilder.newBuilder(AccessTokenCreationRestException.class)
                    .withErrorCode(AccessTokenCreationRestException.INVALID_DURATION)
                    .addParameter("default_duration", defaultDuration)
                    .addParameter("duration_seconds", Long.valueOf(duration.getSeconds()))
                    .build();
            } else {
                return Pair.of(defaultDuration, Optional.of(duration));
            }
        }
        return Pair.of(defaultDuration, Optional.empty());
    }

    private String getErrorCode(AuthCodeFlowErrorCode error) {
        if (error == AuthCodeFlowErrorCode.USER_NOT_FOUND || error == AuthCodeFlowErrorCode.USER_RESTRICTED) {
            return error.name().toLowerCase();
        } else {
            return OIDC_AUTH_ERROR_CODE_PREFIX + error.ordinal();
        }
    }

    private AccessTokenResponse.Type getType(ClientAuthorization authorization) {
        if (authorization instanceof ResourceAuthorization) {
            return AccessTokenResponse.Type.RESOURCE;
        } else if (authorization instanceof ManagedAuthorization) {
            return AccessTokenResponse.Type.MANAGED;
        } else {
            return AccessTokenResponse.Type.USER;
        }
    }
}
