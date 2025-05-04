package com.extole.client.rest.impl.client;

import static com.extole.client.rest.client.ManagedAccessTokenRestException.AUTHORIZATION_CODE_MISSING_CODE;
import static com.extole.client.rest.client.ManagedAccessTokenRestException.AUTHORIZATION_CODE_MISSING_CSRF;
import static com.extole.client.rest.client.ManagedAccessTokenRestException.AUTHORIZATION_CODE_MISSING_NONCE;
import static com.extole.client.rest.client.ManagedAccessTokenRestException.AUTHORIZATION_CODE_MISSING_STATE;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.authorization.service.client.managed.AuthCodeSensitiveOperationAccessDeniedException;
import com.extole.authorization.service.client.managed.ManagedAuthorization;
import com.extole.authorization.service.client.managed.ManagedAuthorizationBuilder;
import com.extole.authorization.service.client.managed.ManagedAuthorizationInvalidNameException;
import com.extole.authorization.service.client.managed.ManagedAuthorizationNegativeDurationException;
import com.extole.authorization.service.client.managed.ManagedAuthorizationNotFoundException;
import com.extole.authorization.service.client.managed.ManagedAuthorizationSensitiveOperationException;
import com.extole.authorization.service.client.managed.ManagedAuthorizationService;
import com.extole.client.rest.client.AccessTokenResponse;
import com.extole.client.rest.client.AuthCodeManagedAccessTokenCreationRequest;
import com.extole.client.rest.client.ManagedAccessTokenCreationRequest;
import com.extole.client.rest.client.ManagedAccessTokenEndpoints;
import com.extole.client.rest.client.ManagedAccessTokenResponse;
import com.extole.client.rest.client.ManagedAccessTokenRestException;
import com.extole.client.rest.client.Scope;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeBadCredentialsException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeDisabledCredentialsException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeExpiredCredentialsException;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeLockedCredentialsException;

@Provider
public class ManagedClientAccessTokenEndpointsImpl implements ManagedAccessTokenEndpoints {

    private static final int NUMBER_OF_DIGITS_MASK = 4;

    private final ClientAuthorizationProvider authorizationProvider;
    private final ManagedAuthorizationService managedAuthorizationService;

    @Autowired
    public ManagedClientAccessTokenEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ManagedAuthorizationService appAuthorizationService) {
        this.authorizationProvider = authorizationProvider;
        this.managedAuthorizationService = appAuthorizationService;
    }

    @Override
    public ManagedAccessTokenResponse createManagedToken(String accessToken,
        Optional<ManagedAccessTokenCreationRequest> managedAccessTokenCreationRequest, ZoneId timeZone)
        throws ManagedAccessTokenRestException, UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        if (managedAccessTokenCreationRequest.isEmpty()
            || Strings.isNullOrEmpty(managedAccessTokenCreationRequest.get().getPassword())) {
            throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                .withErrorCode(ManagedAccessTokenRestException.CREDENTIALS_MISSING).build();
        }

        try {
            ManagedAccessTokenCreationRequest request = managedAccessTokenCreationRequest.get();
            ManagedAuthorizationBuilder builder = managedAuthorizationService.create(authorization)
                .withPasswordSensitiveOperationAccess(request.getPassword());
            request.getName().ifPresent(name -> builder.withName(name));
            request.getScopes().ifPresent(scopes -> builder.withScopes(toServiceScopes(scopes)));
            request.getDurationSeconds()
                .ifPresent(durationSeconds -> builder.withDuration(Duration.ofSeconds(durationSeconds.longValue())));
            return toManagedAccessTokenResponse(builder.build(), false, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ManagedAuthorizationNegativeDurationException e) {
            throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                .withErrorCode(ManagedAccessTokenRestException.INVALID_DURATION)
                .withCause(e)
                .build();
        } catch (ManagedAuthorizationInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                .withErrorCode(ManagedAccessTokenRestException.INVALID_NAME)
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .build();
        } catch (ManagedAuthorizationSensitiveOperationException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ExtoleAuthProviderTypeBadCredentialsException) {
                throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                    .withCause(e)
                    .withErrorCode(ManagedAccessTokenRestException.CREDENTIALS_INVALID)
                    .build();
            }
            if (cause instanceof ExtoleAuthProviderTypeLockedCredentialsException) {
                throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                    .withCause(e)
                    .withErrorCode(ManagedAccessTokenRestException.ACCOUNT_LOCKED)
                    .build();
            }
            if (cause instanceof ExtoleAuthProviderTypeDisabledCredentialsException) {
                throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                    .withCause(e)
                    .withErrorCode(ManagedAccessTokenRestException.ACCOUNT_DISABLED)
                    .build();
            }
            if (cause instanceof ExtoleAuthProviderTypeExpiredCredentialsException) {
                throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                    .withCause(e)
                    .withErrorCode(ManagedAccessTokenRestException.CREDENTIALS_EXPIRED)
                    .build();
            }
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public ManagedAccessTokenResponse createManagedToken(String accessToken,
        AuthCodeManagedAccessTokenCreationRequest authCodeManagedAccessTokenCreationRequest, String csrfToken,
        String nonce, ZoneId timeZone) throws ManagedAccessTokenRestException, UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Long durationSeconds = authCodeManagedAccessTokenCreationRequest.getDurationSeconds();
        try {

            if (authCodeManagedAccessTokenCreationRequest.getCode() == null) {
                throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                    .withErrorCode(AUTHORIZATION_CODE_MISSING_CODE)
                    .build();
            }
            if (authCodeManagedAccessTokenCreationRequest.getState() == null) {
                throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                    .withErrorCode(AUTHORIZATION_CODE_MISSING_STATE)
                    .build();
            }
            if (csrfToken == null) {
                throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                    .withErrorCode(AUTHORIZATION_CODE_MISSING_CSRF)
                    .build();
            }
            if (nonce == null) {
                throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                    .withErrorCode(AUTHORIZATION_CODE_MISSING_NONCE)
                    .build();
            }

            ManagedAuthorizationBuilder managedAuthorizationBuilder = managedAuthorizationService.create(authorization)
                .withAuthCodeSensitiveOperationAccess()
                .withCode(authCodeManagedAccessTokenCreationRequest.getCode())
                .withState(authCodeManagedAccessTokenCreationRequest.getState())
                .withCsrfToken(csrfToken)
                .withNonce(nonce)
                .done()
                .withName(authCodeManagedAccessTokenCreationRequest.getName())
                .withScopes(toServiceScopes(authCodeManagedAccessTokenCreationRequest.getScopes()));
            if (durationSeconds != null) {
                managedAuthorizationBuilder.withDuration(Duration.ofSeconds(durationSeconds.longValue()));
            }
            return toManagedAccessTokenResponse(managedAuthorizationBuilder.build(), false, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AuthCodeSensitiveOperationAccessDeniedException e) {
            throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                .withErrorCode(ManagedAccessTokenRestException.AUTHORIZATION_CODE_RESPONSE_INVALID)
                .withCause(e)
                .build();
        } catch (ManagedAuthorizationNegativeDurationException e) {
            throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                .withErrorCode(ManagedAccessTokenRestException.INVALID_DURATION)
                .withCause(e)
                .build();
        } catch (ManagedAuthorizationInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                .withErrorCode(ManagedAccessTokenRestException.INVALID_NAME)
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .build();
        }
    }

    @Override
    public List<ManagedAccessTokenResponse> getTokens(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<ManagedAuthorization> managedAccessTokenList = managedAuthorizationService.getAll(authorization);
            return managedAccessTokenList.stream()
                .map(managedAuthorization -> toManagedAccessTokenResponse(managedAuthorization, true, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withCause(e)
                .build();
        }
    }

    @Override
    public void delete(String accessToken, String accessTokenId, ZoneId timeZone)
        throws ManagedAccessTokenRestException, UserAuthorizationRestException {
        try {
            managedAuthorizationService.delete(authorizationProvider.getClientAuthorization(accessToken),
                Id.valueOf(accessTokenId));
        } catch (ManagedAuthorizationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ManagedAccessTokenRestException.class)
                .withErrorCode(ManagedAccessTokenRestException.NO_SUCH_MANAGED_TOKEN)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private ManagedAccessTokenResponse toManagedAccessTokenResponse(ManagedAuthorization managedAuthorization,
        boolean maskAccessToken, ZoneId timeZone) {

        String accessToken = maskAccessToken ? getLastDigits(managedAuthorization.getAccessToken())
            : managedAuthorization.getAccessToken();
        AccessTokenResponse accessTokenResponse = maskAccessToken ? null : toAccessTokenResponse(managedAuthorization);

        return ManagedAccessTokenResponse.builder()
            .withAccessTokenId(managedAuthorization.getId().getValue())
            .withClientId(managedAuthorization.getClientId().getValue())
            .withExpireDate(managedAuthorization.getExpiresAt().atZone(timeZone))
            .withName(managedAuthorization.getName())
            .withCreateDate(managedAuthorization.getCreatedAt().atZone(timeZone))
            .withAccessToken(accessToken)
            .withAccessTokenResponse(Optional.ofNullable(accessTokenResponse))
            .build();
    }

    private AccessTokenResponse toAccessTokenResponse(@Nullable ManagedAuthorization authorization) {
        if (authorization != null) {
            long expiresAt = authorization.getExpiresAt().getEpochSecond();
            long expiresInSeconds = expiresAt - Instant.now().getEpochSecond();
            Set<Scope> scopes = authorization.getScopes().stream().map(scope -> Scope.valueOf(scope.toString()))
                .collect(Collectors.toSet());

            return AccessTokenResponse.builder()
                .withType(AccessTokenResponse.Type.MANAGED)
                .withToken(authorization.getAccessToken())
                .withClientId(authorization.getClientId().getValue())
                .withIdentityId(authorization.getIdentityId().getValue())
                .withExpiresIn(expiresInSeconds)
                .withScopes(scopes)
                .build();
        }
        return null;
    }

    private Set<Authorization.Scope> toServiceScopes(Set<Scope> scopes) {
        if (scopes == null) {
            return Collections.emptySet();
        }
        return scopes.stream()
            .map(scope -> Authorization.Scope.valueOf(scope.name()))
            .collect(Collectors.toSet());
    }

    private String getLastDigits(String accessToken) {
        return accessToken.substring(accessToken.length() - NUMBER_OF_DIGITS_MASK);
    }

}
