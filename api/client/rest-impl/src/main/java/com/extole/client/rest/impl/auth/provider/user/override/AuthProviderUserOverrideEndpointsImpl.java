package com.extole.client.rest.impl.auth.provider.user.override;

import static com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideValidationRestException.AUTH_PROVIDER_USER_OVERRIDE_ALREADY_DEFINED;
import static com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideValidationRestException.AUTH_PROVIDER_USER_OVERRIDE_INVALID_DESCRIPTION;
import static com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideValidationRestException.AUTH_PROVIDER_USER_OVERRIDE_INVALID_NAME;
import static com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideValidationRestException.AUTH_PROVIDER_USER_OVERRIDE_MISSING_AUTH_PROVIDER_ENABLED;
import static com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideValidationRestException.AUTH_PROVIDER_USER_OVERRIDE_MISSING_USER;
import static com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideValidationRestException.AUTH_PROVIDER_USER_OVERRIDE_USER_NOT_FOUND;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.auth.provider.AuthProviderRestException;
import com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideCreateRequest;
import com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideEndpoints;
import com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideResponse;
import com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideRestException;
import com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideUpdateRequest;
import com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.auth.provider.AuthProvider;
import com.extole.model.entity.client.auth.provider.user.override.AuthProviderUserOverride;
import com.extole.model.service.client.auth.provider.AuthProviderBuilder;
import com.extole.model.service.client.auth.provider.AuthProviderNotFoundException;
import com.extole.model.service.client.auth.provider.AuthProviderService;
import com.extole.model.service.client.auth.provider.user.override.AuthProviderUserOverrideAlreadyDefinedException;
import com.extole.model.service.client.auth.provider.user.override.AuthProviderUserOverrideBuilder;
import com.extole.model.service.client.auth.provider.user.override.InvalidAuthProviderUserOverrideDescriptionException;
import com.extole.model.service.client.auth.provider.user.override.InvalidAuthProviderUserOverrideNameException;
import com.extole.model.service.client.auth.provider.user.override.MissingAuthProviderUserOverrideAuthProviderEnabledException;
import com.extole.model.service.client.auth.provider.user.override.MissingAuthProviderUserOverrideUserIdException;
import com.extole.model.service.user.UserNotFoundException;

@Provider
public class AuthProviderUserOverrideEndpointsImpl implements AuthProviderUserOverrideEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final AuthProviderService authProviderService;
    private final AuthProviderUserOverrideRestMapper authProviderUserOverrideRestMapper;

    @Autowired
    public AuthProviderUserOverrideEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        AuthProviderService authProviderService,
        AuthProviderUserOverrideRestMapper authProviderUserOverrideRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.authProviderService = authProviderService;
        this.authProviderUserOverrideRestMapper = authProviderUserOverrideRestMapper;
    }

    @Override
    public List<AuthProviderUserOverrideResponse> listAuthProviderUserOverrides(String accessToken,
        String authProviderId, ZoneId timeZone) throws UserAuthorizationRestException, AuthProviderRestException {
        return getAuthProvider(accessToken, Id.valueOf(authProviderId)).getUserOverrides()
            .stream()
            .map(authProviderUserOverride -> authProviderUserOverrideRestMapper
                .toAuthProviderUserOverrideResponse(authProviderUserOverride, timeZone))
            .collect(Collectors.toList());
    }

    @Override
    public AuthProviderUserOverrideResponse getAuthProviderUserOverride(String accessToken, String authProviderId,
        String overrideId, ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderRestException, AuthProviderUserOverrideRestException {
        return authProviderUserOverrideRestMapper.toAuthProviderUserOverrideResponse(
            getAuthProviderUserOverride(accessToken, Id.valueOf(authProviderId), Id.valueOf(overrideId)), timeZone);
    }

    @Override
    public AuthProviderUserOverrideResponse createAuthProviderUserOverride(String accessToken, String authProviderId,
        AuthProviderUserOverrideCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderRestException,
        AuthProviderUserOverrideValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        AuthProviderUserOverrideBuilder authProviderUserOverrideBuilder =
            getUpdateAuthProviderBuilder(authorization, Id.valueOf(authProviderId)).addUserOverride();

        try {
            if (createRequest.getName() != null) {
                authProviderUserOverrideBuilder.withName(createRequest.getName());
            }
            if (createRequest.getUserId() != null) {
                authProviderUserOverrideBuilder.withUserId(Id.valueOf(createRequest.getUserId()));
            }
            if (createRequest.isAuthProviderEnabledForUser() != null) {
                authProviderUserOverrideBuilder
                    .withAuthProviderEnabledForUser(createRequest.isAuthProviderEnabledForUser());
            }
            if (createRequest.getDescription() != null) {
                authProviderUserOverrideBuilder.withDescription(createRequest.getDescription());
            }

            return authProviderUserOverrideRestMapper
                .toAuthProviderUserOverrideResponse(authProviderUserOverrideBuilder.save(), timeZone);

        } catch (InvalidAuthProviderUserOverrideNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderUserOverrideValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_USER_OVERRIDE_INVALID_NAME)
                .addParameter("name", createRequest.getName())
                .withCause(e)
                .build();
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderUserOverrideValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_USER_OVERRIDE_USER_NOT_FOUND)
                .addParameter("user_id", createRequest.getUserId())
                .withCause(e)
                .build();
        } catch (AuthProviderUserOverrideAlreadyDefinedException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderUserOverrideValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_USER_OVERRIDE_ALREADY_DEFINED)
                .addParameter("user_id", createRequest.getUserId())
                .withCause(e)
                .build();
        } catch (MissingAuthProviderUserOverrideUserIdException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderUserOverrideValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_USER_OVERRIDE_MISSING_USER)
                .withCause(e)
                .build();
        } catch (MissingAuthProviderUserOverrideAuthProviderEnabledException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderUserOverrideValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_USER_OVERRIDE_MISSING_AUTH_PROVIDER_ENABLED)
                .withCause(e)
                .build();
        } catch (InvalidAuthProviderUserOverrideDescriptionException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderUserOverrideValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_USER_OVERRIDE_INVALID_DESCRIPTION)
                .addParameter("description", createRequest.getDescription())
                .withCause(e)
                .build();
        }
    }

    @Override
    public AuthProviderUserOverrideResponse updateAuthProviderUserOverride(String accessToken, String authProviderId,
        String overrideId, AuthProviderUserOverrideUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderRestException, AuthProviderUserOverrideRestException,
        AuthProviderUserOverrideValidationRestException {

        AuthProviderUserOverride authProviderUserOverride =
            getAuthProviderUserOverride(accessToken, Id.valueOf(authProviderId), Id.valueOf(overrideId));
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        AuthProviderUserOverrideBuilder authProviderUserOverrideBuilder =
            getUpdateAuthProviderBuilder(authorization, Id.valueOf(authProviderId))
                .updateUserOverride(authProviderUserOverride);

        try {
            if (updateRequest.getName() != null) {
                authProviderUserOverrideBuilder.withName(updateRequest.getName());
            }
            if (updateRequest.isAuthProviderEnabledForUser() != null) {
                authProviderUserOverrideBuilder
                    .withAuthProviderEnabledForUser(updateRequest.isAuthProviderEnabledForUser().booleanValue());
            }
            if (updateRequest.getDescription() != null) {
                authProviderUserOverrideBuilder.withDescription(updateRequest.getDescription());
            }

            return authProviderUserOverrideRestMapper
                .toAuthProviderUserOverrideResponse(authProviderUserOverrideBuilder.save(), timeZone);

        } catch (InvalidAuthProviderUserOverrideNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderUserOverrideValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_USER_OVERRIDE_INVALID_NAME)
                .addParameter("name", updateRequest.getName())
                .withCause(e)
                .build();
        } catch (InvalidAuthProviderUserOverrideDescriptionException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderUserOverrideValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_USER_OVERRIDE_INVALID_DESCRIPTION)
                .addParameter("description", updateRequest.getDescription())
                .withCause(e)
                .build();
        } catch (MissingAuthProviderUserOverrideUserIdException
            | AuthProviderUserOverrideAlreadyDefinedException
            | MissingAuthProviderUserOverrideAuthProviderEnabledException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AuthProviderUserOverrideResponse archiveAuthProviderUserOverride(String accessToken, String authProviderId,
        String overrideId, ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderRestException, AuthProviderUserOverrideRestException {

        AuthProviderUserOverride authProviderUserOverride =
            getAuthProviderUserOverride(accessToken, Id.valueOf(authProviderId), Id.valueOf(overrideId));
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        AuthProviderUserOverrideBuilder authProviderUserOverrideBuilder =
            getUpdateAuthProviderBuilder(authorization, Id.valueOf(authProviderId))
                .updateUserOverride(authProviderUserOverride);
        try {
            return authProviderUserOverrideRestMapper
                .toAuthProviderUserOverrideResponse(authProviderUserOverrideBuilder
                    .withArchived()
                    .save(), timeZone);
        } catch (MissingAuthProviderUserOverrideUserIdException
            | AuthProviderUserOverrideAlreadyDefinedException
            | MissingAuthProviderUserOverrideAuthProviderEnabledException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private AuthProviderBuilder getUpdateAuthProviderBuilder(Authorization authorization,
        Id<AuthProvider> authProviderId)
        throws AuthProviderRestException, UserAuthorizationRestException {
        try {
            return authProviderService.updateAuthProvider(authorization, authProviderId);
        } catch (AuthProviderNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderRestException.class)
                .withErrorCode(AuthProviderRestException.AUTH_PROVIDER_NOT_FOUND)
                .addParameter("auth_provider_id", authProviderId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private AuthProviderUserOverride getAuthProviderUserOverride(String accessToken,
        Id<AuthProvider> authProviderId, Id<AuthProviderUserOverride> authProviderUserOverrideId)
        throws AuthProviderUserOverrideRestException, UserAuthorizationRestException, AuthProviderRestException {
        return getAuthProvider(accessToken, authProviderId).getUserOverrides()
            .stream()
            .filter(authProviderUserOverride -> authProviderUserOverride.getId().equals(authProviderUserOverrideId))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(AuthProviderUserOverrideRestException.class)
                .withErrorCode(AuthProviderUserOverrideRestException.AUTH_PROVIDER_USER_OVERRIDE_NOT_FOUND)
                .addParameter("auth_provider_user_override_id", authProviderUserOverrideId.getValue())
                .build());
    }

    private AuthProvider getAuthProvider(String accessToken, Id<AuthProvider> authProviderId)
        throws UserAuthorizationRestException, AuthProviderRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return authProviderService.getAuthProvider(authorization, authProviderId);
        } catch (AuthProviderNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderRestException.class)
                .withErrorCode(AuthProviderRestException.AUTH_PROVIDER_NOT_FOUND)
                .addParameter("auth_provider_id", authProviderId.getValue())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

}
