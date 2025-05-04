package com.extole.client.rest.impl.auth.provider.type.extole;

import static com.extole.common.rest.exception.FatalRestRuntimeException.SOFTWARE_ERROR;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.auth.provider.type.AuthProviderTypeValidationRestException;
import com.extole.client.rest.auth.provider.type.extole.ExtoleAuthProviderTypeCreateRequest;
import com.extole.client.rest.auth.provider.type.extole.ExtoleAuthProviderTypeEndpoints;
import com.extole.client.rest.auth.provider.type.extole.ExtoleAuthProviderTypeResponse;
import com.extole.client.rest.auth.provider.type.extole.ExtoleAuthProviderTypeRestException;
import com.extole.client.rest.auth.provider.type.extole.ExtoleAuthProviderTypeUpdateRequest;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.auth.provider.type.InvalidAuthProviderTypeDescriptionException;
import com.extole.model.service.auth.provider.type.InvalidAuthProviderTypeNameException;
import com.extole.model.service.auth.provider.type.MissingAuthProviderTypeNameException;
import com.extole.model.service.auth.provider.type.MissingAuthProviderTypeScopesException;
import com.extole.model.service.auth.provider.type.extole.ExtoleAuthProviderType;
import com.extole.model.service.auth.provider.type.extole.ExtoleAuthProviderTypeBuilder;
import com.extole.model.service.auth.provider.type.extole.ExtoleAuthProviderTypeNotFoundException;
import com.extole.model.service.auth.provider.type.extole.ExtoleAuthProviderTypeService;

@Provider
public class ExtoleAuthProviderTypeEndpointsImpl implements ExtoleAuthProviderTypeEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final ExtoleAuthProviderTypeService extoleAuthProviderTypeService;
    private final ExtoleAuthProviderTypeRestMapper extoleAuthProviderTypeRestMapper;

    @Autowired
    public ExtoleAuthProviderTypeEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ExtoleAuthProviderTypeService extoleAuthProviderTypeService,
        ExtoleAuthProviderTypeRestMapper extoleAuthProviderTypeRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.extoleAuthProviderTypeService = extoleAuthProviderTypeService;
        this.extoleAuthProviderTypeRestMapper = extoleAuthProviderTypeRestMapper;
    }

    @Override
    public List<ExtoleAuthProviderTypeResponse> listExtoleAuthProviderTypes(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return extoleAuthProviderTypeService.createExtoleAuthProviderTypeQueryBuilder(authorization).list()
                .stream()
                .map(extoleAuthProviderType -> extoleAuthProviderTypeRestMapper
                    .toExtoleAuthProviderTypeResponse(extoleAuthProviderType,
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
    public ExtoleAuthProviderTypeResponse getExtoleAuthProviderType(String accessToken, String authProviderTypeId,
        ZoneId timeZone) throws UserAuthorizationRestException, ExtoleAuthProviderTypeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ExtoleAuthProviderType extoleAuthProviderType =
                extoleAuthProviderTypeService.getExtoleAuthProviderType(authorization,
                    Id.valueOf(authProviderTypeId));
            return extoleAuthProviderTypeRestMapper.toExtoleAuthProviderTypeResponse(extoleAuthProviderType,
                timeZone);
        } catch (ExtoleAuthProviderTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ExtoleAuthProviderTypeRestException.class)
                .withErrorCode(ExtoleAuthProviderTypeRestException.EXTOLE_AUTH_PROVIDER_TYPE_NOT_FOUND)
                .addParameter("extole_auth_provider_type_id", authProviderTypeId)
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
    public ExtoleAuthProviderTypeResponse createExtoleAuthProviderType(String accessToken,
        ExtoleAuthProviderTypeCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            ExtoleAuthProviderTypeBuilder extoleAuthProviderTypeBuilder =
                extoleAuthProviderTypeService.createExtoleAuthProviderType(authorization);

            if (createRequest.getName() != null) {
                extoleAuthProviderTypeBuilder.withName(createRequest.getName());
            }

            if (!createRequest.getScopes().isEmpty()) {
                extoleAuthProviderTypeBuilder.withScopes(createRequest.getScopes()
                    .stream()
                    .map(Enum::name)
                    .map(Authorization.Scope::valueOf)
                    .collect(Collectors.toSet()));
            }

            if (createRequest.getDescription() != null) {
                extoleAuthProviderTypeBuilder.withDescription(createRequest.getDescription());
            }

            ExtoleAuthProviderType extoleAuthProviderType = extoleAuthProviderTypeBuilder.save();
            return extoleAuthProviderTypeRestMapper.toExtoleAuthProviderTypeResponse(extoleAuthProviderType,
                timeZone);

        } catch (InvalidAuthProviderTypeNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_INVALID_NAME)
                .addParameter("name", createRequest.getName())
                .withCause(e)
                .build();
        } catch (InvalidAuthProviderTypeDescriptionException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_INVALID_DESCRIPTION)
                .addParameter("description", createRequest.getDescription())
                .withCause(e)
                .build();
        } catch (MissingAuthProviderTypeNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_MISSING_NAME)
                .withCause(e)
                .build();
        } catch (MissingAuthProviderTypeScopesException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_MISSING_SCOPES)
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
    public ExtoleAuthProviderTypeResponse updateExtoleAuthProviderType(String accessToken, String authProviderTypeId,
        ExtoleAuthProviderTypeUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, ExtoleAuthProviderTypeRestException,
        AuthProviderTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            ExtoleAuthProviderTypeBuilder extoleAuthProviderTypeBuilder =
                extoleAuthProviderTypeService.updateExtoleAuthProviderType(authorization,
                    Id.valueOf(authProviderTypeId));

            if (updateRequest.getName() != null) {
                extoleAuthProviderTypeBuilder.withName(updateRequest.getName());
            }

            if (updateRequest.getScopes() != null) {
                extoleAuthProviderTypeBuilder.withScopes(updateRequest.getScopes()
                    .stream()
                    .map(Enum::name)
                    .map(Authorization.Scope::valueOf)
                    .collect(Collectors.toSet()));
            }

            if (updateRequest.getDescription() != null) {
                extoleAuthProviderTypeBuilder.withDescription(updateRequest.getDescription());
            }

            ExtoleAuthProviderType extoleAuthProviderType = extoleAuthProviderTypeBuilder.save();
            return extoleAuthProviderTypeRestMapper.toExtoleAuthProviderTypeResponse(extoleAuthProviderType,
                timeZone);

        } catch (ExtoleAuthProviderTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ExtoleAuthProviderTypeRestException.class)
                .withErrorCode(ExtoleAuthProviderTypeRestException.EXTOLE_AUTH_PROVIDER_TYPE_NOT_FOUND)
                .addParameter("extole_auth_provider_type_id", authProviderTypeId)
                .withCause(e)
                .build();
        } catch (InvalidAuthProviderTypeNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_INVALID_NAME)
                .addParameter("name", updateRequest.getName())
                .withCause(e)
                .build();
        } catch (InvalidAuthProviderTypeDescriptionException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_INVALID_DESCRIPTION)
                .addParameter("description", updateRequest.getDescription())
                .withCause(e)
                .build();
        } catch (MissingAuthProviderTypeNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_MISSING_NAME)
                .withCause(e)
                .build();
        } catch (MissingAuthProviderTypeScopesException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_MISSING_SCOPES)
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
    public ExtoleAuthProviderTypeResponse archiveExtoleAuthProviderType(String accessToken, String authProviderTypeId,
        ZoneId timeZone) throws UserAuthorizationRestException, ExtoleAuthProviderTypeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ExtoleAuthProviderType extoleAuthProviderType =
                extoleAuthProviderTypeService
                    .updateExtoleAuthProviderType(authorization, Id.valueOf(authProviderTypeId))
                    .withArchived()
                    .save();
            return extoleAuthProviderTypeRestMapper.toExtoleAuthProviderTypeResponse(extoleAuthProviderType,
                timeZone);
        } catch (ExtoleAuthProviderTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ExtoleAuthProviderTypeRestException.class)
                .withErrorCode(ExtoleAuthProviderTypeRestException.EXTOLE_AUTH_PROVIDER_TYPE_NOT_FOUND)
                .addParameter("extole_auth_provider_type_id", authProviderTypeId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (MissingAuthProviderTypeNameException | MissingAuthProviderTypeScopesException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

}
