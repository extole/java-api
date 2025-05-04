package com.extole.client.rest.impl.auth.provider;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.auth.provider.AuthProviderCreateRequest;
import com.extole.client.rest.auth.provider.AuthProviderEndpoints;
import com.extole.client.rest.auth.provider.AuthProviderResponse;
import com.extole.client.rest.auth.provider.AuthProviderRestException;
import com.extole.client.rest.auth.provider.AuthProviderUpdateRequest;
import com.extole.client.rest.auth.provider.AuthProviderValidationRestException;
import com.extole.client.rest.auth.provider.type.AuthProviderTypeRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.auth.provider.AuthProvider;
import com.extole.model.service.auth.provider.type.AuthProviderTypeNotFoundException;
import com.extole.model.service.client.auth.provider.AuthProviderBuilder;
import com.extole.model.service.client.auth.provider.AuthProviderNotFoundException;
import com.extole.model.service.client.auth.provider.AuthProviderService;
import com.extole.model.service.client.auth.provider.InvalidAuthProviderDescriptionException;
import com.extole.model.service.client.auth.provider.InvalidAuthProviderNameException;
import com.extole.model.service.client.auth.provider.MissingAuthProviderNameException;
import com.extole.model.service.client.auth.provider.MissingAuthProviderTypeIdException;
import com.extole.model.service.client.auth.provider.user.override.AuthProviderUserOverrideAlreadyDefinedException;
import com.extole.model.service.client.auth.provider.user.override.MissingAuthProviderUserOverrideAuthProviderEnabledException;
import com.extole.model.service.client.auth.provider.user.override.MissingAuthProviderUserOverrideUserIdException;

@Provider
public class AuthProviderEndpointsImpl implements AuthProviderEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final AuthProviderService authProviderService;
    private final AuthProviderRestMapper authProviderRestMapper;

    @Autowired
    public AuthProviderEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        AuthProviderService authProviderService,
        AuthProviderRestMapper authProviderRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.authProviderService = authProviderService;
        this.authProviderRestMapper = authProviderRestMapper;
    }

    @Override
    public List<AuthProviderResponse> listAuthProviders(String accessToken,
        ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return authProviderService.createAuthProviderQueryBuilder(authorization)
                .list()
                .stream()
                .map(authProvider -> authProviderRestMapper
                    .toAuthProviderResponse(authProvider,
                        timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AuthProviderResponse getAuthProvider(String accessToken,
        String authProviderId,
        ZoneId timeZone) throws UserAuthorizationRestException, AuthProviderRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            AuthProvider authProvider = authProviderService.getAuthProvider(authorization, Id.valueOf(authProviderId));
            return authProviderRestMapper.toAuthProviderResponse(
                authProvider,
                timeZone);
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

    @Override
    public AuthProviderResponse createAuthProvider(String accessToken,
        AuthProviderCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderTypeRestException,
        AuthProviderValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            AuthProviderBuilder authProviderBuilder = authProviderService.createAuthProvider(authorization);

            if (createRequest.getName() != null) {
                authProviderBuilder.withName(createRequest.getName());
            }

            if (createRequest.getAuthProviderTypeId() != null) {
                authProviderBuilder
                    .withAuthProviderTypeId(Id.valueOf(createRequest.getAuthProviderTypeId()));
            }

            authProviderBuilder
                .withDefaultEnabledForAllUsers(Boolean.TRUE.equals(createRequest.isDefaultEnabledForAllUsers()));

            if (createRequest.getDescription() != null) {
                authProviderBuilder.withDescription(createRequest.getDescription());
            }

            AuthProvider authProvider = authProviderBuilder.save();
            return authProviderRestMapper.toAuthProviderResponse(
                authProvider,
                timeZone);

        } catch (InvalidAuthProviderNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderValidationRestException.class)
                .withErrorCode(AuthProviderValidationRestException.AUTH_PROVIDER_INVALID_NAME)
                .addParameter("name", createRequest.getName())
                .withCause(e)
                .build();
        } catch (MissingAuthProviderNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderValidationRestException.class)
                .withErrorCode(AuthProviderValidationRestException.AUTH_PROVIDER_MISSING_NAME)
                .withCause(e)
                .build();
        } catch (AuthProviderTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeRestException.class)
                .withErrorCode(AuthProviderTypeRestException.AUTH_PROVIDER_TYPE_NOT_FOUND)
                .addParameter("auth_provider_type_id", createRequest.getAuthProviderTypeId())
                .withCause(e)
                .build();
        } catch (MissingAuthProviderTypeIdException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderValidationRestException.class)
                .withErrorCode(AuthProviderValidationRestException.AUTH_PROVIDER_MISSING_TYPE)
                .build();
        } catch (InvalidAuthProviderDescriptionException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderValidationRestException.class)
                .withErrorCode(AuthProviderValidationRestException.AUTH_PROVIDER_INVALID_DESCRIPTION)
                .addParameter("description", createRequest.getDescription())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
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
    public AuthProviderResponse updateAuthProvider(String accessToken,
        String authProviderId,
        AuthProviderUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderRestException,
        AuthProviderTypeRestException, AuthProviderValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            AuthProviderBuilder authProviderBuilder = authProviderService.updateAuthProvider(authorization,
                Id.valueOf(authProviderId));

            if (updateRequest.getName() != null) {
                authProviderBuilder.withName(updateRequest.getName());
            }
            if (updateRequest.getAuthProviderTypeId() != null) {
                authProviderBuilder
                    .withAuthProviderTypeId(Id.valueOf(updateRequest.getAuthProviderTypeId()));
            }

            if (updateRequest.isDefaultEnabledForAllUsers() != null) {
                authProviderBuilder
                    .withDefaultEnabledForAllUsers(updateRequest.isDefaultEnabledForAllUsers().booleanValue());
            }

            if (updateRequest.getDescription() != null) {
                authProviderBuilder.withDescription(updateRequest.getDescription());
            }

            AuthProvider authProvider = authProviderBuilder.save();
            return authProviderRestMapper.toAuthProviderResponse(
                authProvider,
                timeZone);

        } catch (AuthProviderNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderRestException.class)
                .withErrorCode(AuthProviderRestException.AUTH_PROVIDER_NOT_FOUND)
                .addParameter("auth_provider_id", authProviderId)
                .withCause(e)
                .build();
        } catch (MissingAuthProviderNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderValidationRestException.class)
                .withErrorCode(AuthProviderValidationRestException.AUTH_PROVIDER_MISSING_NAME)
                .withCause(e)
                .build();
        } catch (AuthProviderTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeRestException.class)
                .withErrorCode(AuthProviderTypeRestException.AUTH_PROVIDER_TYPE_NOT_FOUND)
                .addParameter("auth_provider_type_id", updateRequest.getAuthProviderTypeId())
                .withCause(e)
                .build();
        } catch (MissingAuthProviderTypeIdException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderValidationRestException.class)
                .withErrorCode(AuthProviderValidationRestException.AUTH_PROVIDER_MISSING_TYPE)
                .build();
        } catch (InvalidAuthProviderNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderValidationRestException.class)
                .withErrorCode(AuthProviderValidationRestException.AUTH_PROVIDER_INVALID_NAME)
                .addParameter("name", updateRequest.getName())
                .withCause(e)
                .build();
        } catch (InvalidAuthProviderDescriptionException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderValidationRestException.class)
                .withErrorCode(AuthProviderValidationRestException.AUTH_PROVIDER_INVALID_DESCRIPTION)
                .addParameter("description", updateRequest.getDescription())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
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
    public AuthProviderResponse archiveAuthProvider(String accessToken,
        String authProviderId,
        ZoneId timeZone) throws UserAuthorizationRestException, AuthProviderRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            AuthProvider authProvider =
                authProviderService.updateAuthProvider(authorization, Id.valueOf(authProviderId))
                    .withArchived()
                    .save();
            return authProviderRestMapper.toAuthProviderResponse(
                authProvider,
                timeZone);
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
        } catch (MissingAuthProviderNameException
            | MissingAuthProviderTypeIdException
            | MissingAuthProviderUserOverrideUserIdException
            | AuthProviderUserOverrideAlreadyDefinedException
            | MissingAuthProviderUserOverrideAuthProviderEnabledException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

}
