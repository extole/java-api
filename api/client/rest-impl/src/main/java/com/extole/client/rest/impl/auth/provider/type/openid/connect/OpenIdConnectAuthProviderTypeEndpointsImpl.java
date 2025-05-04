package com.extole.client.rest.impl.auth.provider.type.openid.connect;

import static com.extole.client.rest.auth.provider.type.AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_INVALID_DESCRIPTION;
import static com.extole.client.rest.auth.provider.type.AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_INVALID_NAME;
import static com.extole.client.rest.auth.provider.type.AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_MISSING_NAME;
import static com.extole.client.rest.auth.provider.type.AuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_MISSING_SCOPES;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeRestException.OPEN_ID_CONNECT_AUTH_PROVIDER_TYPE_NOT_FOUND;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_APP_SECRET_NOT_BASE64;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_CUSTOM_PARAMS_TOTAL_LENGTH_TOO_LONG;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_INVALID_APP_ID;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_INVALID_APP_SECRET;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_INVALID_CUSTOM_PARAM;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_INVALID_DOMAIN;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_MISSING_APP_ID;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_MISSING_APP_SECRET;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_MISSING_CATEGORY;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_MISSING_DOMAIN;
import static com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException.AUTH_PROVIDER_TYPE_TOO_MANY_CUSTOM_PARAMS;
import static com.extole.common.rest.exception.FatalRestRuntimeException.SOFTWARE_ERROR;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.auth.provider.type.AuthProviderTypeValidationRestException;
import com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeCreateRequest;
import com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeEndpoints;
import com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeResponse;
import com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeRestException;
import com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeUpdateRequest;
import com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.auth.provider.type.InvalidAuthProviderTypeDescriptionException;
import com.extole.model.service.auth.provider.type.InvalidAuthProviderTypeNameException;
import com.extole.model.service.auth.provider.type.MissingAuthProviderTypeNameException;
import com.extole.model.service.auth.provider.type.MissingAuthProviderTypeScopesException;
import com.extole.model.service.auth.provider.type.MissingOpenIdConnectAuthProviderTypeCategoryException;
import com.extole.model.service.auth.provider.type.openid.connect.Category;
import com.extole.model.service.auth.provider.type.openid.connect.InvalidOpenIdConnectAuthProviderTypeApplicationIdException;
import com.extole.model.service.auth.provider.type.openid.connect.InvalidOpenIdConnectAuthProviderTypeApplicationSecretException;
import com.extole.model.service.auth.provider.type.openid.connect.InvalidOpenIdConnectAuthProviderTypeCustomParamCountException;
import com.extole.model.service.auth.provider.type.openid.connect.InvalidOpenIdConnectAuthProviderTypeCustomParameterException;
import com.extole.model.service.auth.provider.type.openid.connect.InvalidOpenIdConnectAuthProviderTypeCustomParamsMaxLengthException;
import com.extole.model.service.auth.provider.type.openid.connect.InvalidOpenIdConnectAuthProviderTypeDomainException;
import com.extole.model.service.auth.provider.type.openid.connect.MissingOpenIdConnectAuthProviderTypeApplicationIdException;
import com.extole.model.service.auth.provider.type.openid.connect.MissingOpenIdConnectAuthProviderTypeApplicationSecretException;
import com.extole.model.service.auth.provider.type.openid.connect.MissingOpenIdConnectAuthProviderTypeDomainException;
import com.extole.model.service.auth.provider.type.openid.connect.OpenIdConnectAuthProviderType;
import com.extole.model.service.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeBuilder;
import com.extole.model.service.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeNotFoundException;
import com.extole.model.service.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeService;

@Provider
public class OpenIdConnectAuthProviderTypeEndpointsImpl implements OpenIdConnectAuthProviderTypeEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final OpenIdConnectAuthProviderTypeService openIdConnectAuthProviderTypeService;
    private final OpenIdConnectAuthProviderTypeRestMapper openIdConnectAuthProviderTypeRestMapper;

    @Autowired
    public OpenIdConnectAuthProviderTypeEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        OpenIdConnectAuthProviderTypeService openIdConnectAuthProviderTypeService,
        OpenIdConnectAuthProviderTypeRestMapper openIdConnectAuthProviderTypeRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.openIdConnectAuthProviderTypeService = openIdConnectAuthProviderTypeService;
        this.openIdConnectAuthProviderTypeRestMapper = openIdConnectAuthProviderTypeRestMapper;
    }

    @Override
    public List<OpenIdConnectAuthProviderTypeResponse> listOpenIdConnectAuthProviderTypes(String accessToken,
        ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return openIdConnectAuthProviderTypeService.createOpenIdConnectAuthProviderTypeQueryBuilder(authorization)
                .list()
                .stream()
                .map(openIdConnectAuthProviderType -> openIdConnectAuthProviderTypeRestMapper
                    .toOpenIdConnectAuthProviderTypeResponse(openIdConnectAuthProviderType,
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
    public OpenIdConnectAuthProviderTypeResponse getOpenIdConnectAuthProviderType(String accessToken,
        String authProviderTypeId,
        ZoneId timeZone) throws UserAuthorizationRestException, OpenIdConnectAuthProviderTypeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            OpenIdConnectAuthProviderType openIdConnectAuthProviderType =
                openIdConnectAuthProviderTypeService.getOpenIdConnectAuthProviderType(authorization,
                    Id.valueOf(authProviderTypeId));
            return openIdConnectAuthProviderTypeRestMapper.toOpenIdConnectAuthProviderTypeResponse(
                openIdConnectAuthProviderType,
                timeZone);
        } catch (OpenIdConnectAuthProviderTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeRestException.class)
                .withErrorCode(OPEN_ID_CONNECT_AUTH_PROVIDER_TYPE_NOT_FOUND)
                .addParameter("oidc_auth_provider_type_id", authProviderTypeId)
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
    public OpenIdConnectAuthProviderTypeResponse createOpenIdConnectAuthProviderType(String accessToken,
        OpenIdConnectAuthProviderTypeCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderTypeValidationRestException,
        OpenIdConnectAuthProviderTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            OpenIdConnectAuthProviderTypeBuilder openIdConnectAuthProviderTypeBuilder =
                openIdConnectAuthProviderTypeService.createOpenIdConnectAuthProviderType(authorization);

            if (createRequest.getName() != null) {
                openIdConnectAuthProviderTypeBuilder.withName(createRequest.getName());
            }

            if (createRequest.getDomain() != null) {
                openIdConnectAuthProviderTypeBuilder.withDomain(createRequest.getDomain());
            }

            if (createRequest.getApplicationId() != null) {
                openIdConnectAuthProviderTypeBuilder.withApplicationId(createRequest.getApplicationId());
            }

            if (createRequest.getApplicationSecret() != null) {
                openIdConnectAuthProviderTypeBuilder
                    .withApplicationSecret(decodeSecret(createRequest.getApplicationSecret()));
            }

            if (createRequest.getCustomParams() != null) {
                openIdConnectAuthProviderTypeBuilder.withCustomParams(createRequest.getCustomParams());
            }

            if (!createRequest.getScopes().isEmpty()) {
                openIdConnectAuthProviderTypeBuilder.withScopes(createRequest.getScopes()
                    .stream()
                    .map(Enum::name)
                    .map(Authorization.Scope::valueOf)
                    .collect(Collectors.toSet()));
            }

            if (createRequest.getCategory() != null) {
                openIdConnectAuthProviderTypeBuilder.withCategory(Category.valueOf(createRequest.getCategory().name()));
            }

            if (createRequest.getDescription() != null) {
                openIdConnectAuthProviderTypeBuilder.withDescription(createRequest.getDescription());
            }

            OpenIdConnectAuthProviderType openIdConnectAuthProviderType = openIdConnectAuthProviderTypeBuilder.save();
            return openIdConnectAuthProviderTypeRestMapper.toOpenIdConnectAuthProviderTypeResponse(
                openIdConnectAuthProviderType,
                timeZone);

        } catch (InvalidAuthProviderTypeNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_NAME)
                .addParameter("name", createRequest.getName())
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeDomainException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_DOMAIN)
                .addParameter("domain", createRequest.getDomain())
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeApplicationIdException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_APP_ID)
                .addParameter("application_id", createRequest.getApplicationId())
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeApplicationSecretException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_APP_SECRET)
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeCustomParamCountException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_TOO_MANY_CUSTOM_PARAMS)
                .addParameter("max_custom_params", Integer.valueOf(e.getMaxCustomParams()))
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeCustomParameterException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_CUSTOM_PARAM)
                .addParameter("key", e.getKey())
                .addParameter("value", e.getValue())
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeCustomParamsMaxLengthException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_CUSTOM_PARAMS_TOTAL_LENGTH_TOO_LONG)
                .withCause(e)
                .build();
        } catch (InvalidAuthProviderTypeDescriptionException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_DESCRIPTION)
                .addParameter("description", createRequest.getDescription())
                .withCause(e)
                .build();
        } catch (MissingAuthProviderTypeNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_MISSING_NAME)
                .withCause(e)
                .build();
        } catch (MissingAuthProviderTypeScopesException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_MISSING_SCOPES)
                .withCause(e)
                .build();
        } catch (MissingOpenIdConnectAuthProviderTypeDomainException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_MISSING_DOMAIN)
                .withCause(e)
                .build();
        } catch (MissingOpenIdConnectAuthProviderTypeApplicationIdException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_MISSING_APP_ID)
                .withCause(e)
                .build();
        } catch (MissingOpenIdConnectAuthProviderTypeApplicationSecretException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_MISSING_APP_SECRET)
                .withCause(e)
                .build();
        } catch (MissingOpenIdConnectAuthProviderTypeCategoryException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_MISSING_CATEGORY)
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
    public OpenIdConnectAuthProviderTypeResponse updateOpenIdConnectAuthProviderType(String accessToken,
        String authProviderTypeId,
        OpenIdConnectAuthProviderTypeUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, OpenIdConnectAuthProviderTypeRestException,
        AuthProviderTypeValidationRestException, OpenIdConnectAuthProviderTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            OpenIdConnectAuthProviderTypeBuilder openIdConnectAuthProviderTypeBuilder =
                openIdConnectAuthProviderTypeService.updateOpenIdConnectAuthProviderType(authorization,
                    Id.valueOf(authProviderTypeId));

            if (updateRequest.getName() != null) {
                openIdConnectAuthProviderTypeBuilder.withName(updateRequest.getName());
            }

            if (updateRequest.getDomain() != null) {
                openIdConnectAuthProviderTypeBuilder.withDomain(updateRequest.getDomain());
            }

            if (updateRequest.getApplicationId() != null) {
                openIdConnectAuthProviderTypeBuilder.withApplicationId(updateRequest.getApplicationId());
            }

            if (updateRequest.getApplicationSecret() != null) {
                openIdConnectAuthProviderTypeBuilder
                    .withApplicationSecret(decodeSecret(updateRequest.getApplicationSecret()));
            }

            if (updateRequest.getCustomParams() != null) {
                openIdConnectAuthProviderTypeBuilder.withCustomParams(updateRequest.getCustomParams());
            }

            if (updateRequest.getScopes() != null) {
                openIdConnectAuthProviderTypeBuilder.withScopes(updateRequest.getScopes()
                    .stream()
                    .map(Enum::name)
                    .map(Authorization.Scope::valueOf)
                    .collect(Collectors.toSet()));
            }

            if (updateRequest.getCategory() != null) {
                openIdConnectAuthProviderTypeBuilder.withCategory(Category.valueOf(updateRequest.getCategory().name()));
            }

            if (updateRequest.getDescription() != null) {
                openIdConnectAuthProviderTypeBuilder.withDescription(updateRequest.getDescription());
            }

            OpenIdConnectAuthProviderType openIdConnectAuthProviderType = openIdConnectAuthProviderTypeBuilder.save();
            return openIdConnectAuthProviderTypeRestMapper.toOpenIdConnectAuthProviderTypeResponse(
                openIdConnectAuthProviderType,
                timeZone);

        } catch (OpenIdConnectAuthProviderTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeRestException.class)
                .withErrorCode(OPEN_ID_CONNECT_AUTH_PROVIDER_TYPE_NOT_FOUND)
                .addParameter("oidc_auth_provider_type_id", authProviderTypeId)
                .withCause(e)
                .build();
        } catch (InvalidAuthProviderTypeNameException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_NAME)
                .addParameter("name", updateRequest.getName())
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeDomainException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_DOMAIN)
                .addParameter("domain", updateRequest.getDomain())
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeApplicationIdException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_APP_ID)
                .addParameter("application_id", updateRequest.getApplicationId())
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeApplicationSecretException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_APP_SECRET)
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeCustomParamCountException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_TOO_MANY_CUSTOM_PARAMS)
                .addParameter("max_custom_params", Integer.valueOf(e.getMaxCustomParams()))
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeCustomParameterException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_CUSTOM_PARAM)
                .addParameter("key", e.getKey())
                .addParameter("value", e.getValue())
                .withCause(e)
                .build();
        } catch (InvalidOpenIdConnectAuthProviderTypeCustomParamsMaxLengthException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_CUSTOM_PARAMS_TOTAL_LENGTH_TOO_LONG)
                .withCause(e)
                .build();
        } catch (InvalidAuthProviderTypeDescriptionException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_INVALID_DESCRIPTION)
                .addParameter("description", updateRequest.getDescription())
                .withCause(e)
                .build();
        } catch (MissingAuthProviderTypeNameException | MissingAuthProviderTypeScopesException
            | MissingOpenIdConnectAuthProviderTypeDomainException
            | MissingOpenIdConnectAuthProviderTypeApplicationIdException
            | MissingOpenIdConnectAuthProviderTypeApplicationSecretException
            | MissingOpenIdConnectAuthProviderTypeCategoryException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(SOFTWARE_ERROR)
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
    public OpenIdConnectAuthProviderTypeResponse archiveOpenIdConnectAuthProviderType(String accessToken,
        String authProviderTypeId,
        ZoneId timeZone) throws UserAuthorizationRestException, OpenIdConnectAuthProviderTypeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            OpenIdConnectAuthProviderType openIdConnectAuthProviderType =
                openIdConnectAuthProviderTypeService
                    .updateOpenIdConnectAuthProviderType(authorization, Id.valueOf(authProviderTypeId))
                    .withArchived()
                    .save();
            return openIdConnectAuthProviderTypeRestMapper.toOpenIdConnectAuthProviderTypeResponse(
                openIdConnectAuthProviderType,
                timeZone);
        } catch (OpenIdConnectAuthProviderTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeRestException.class)
                .withErrorCode(OPEN_ID_CONNECT_AUTH_PROVIDER_TYPE_NOT_FOUND)
                .addParameter("oidc_auth_provider_type_id", authProviderTypeId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (MissingAuthProviderTypeNameException | MissingAuthProviderTypeScopesException
            | MissingOpenIdConnectAuthProviderTypeApplicationSecretException
            | MissingOpenIdConnectAuthProviderTypeDomainException
            | MissingOpenIdConnectAuthProviderTypeApplicationIdException
            | MissingOpenIdConnectAuthProviderTypeCategoryException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private byte[] decodeSecret(String key) throws OpenIdConnectAuthProviderTypeValidationRestException {
        try {
            return Base64.getDecoder().decode(key.getBytes(StandardCharsets.ISO_8859_1));
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(OpenIdConnectAuthProviderTypeValidationRestException.class)
                .withErrorCode(AUTH_PROVIDER_TYPE_APP_SECRET_NOT_BASE64)
                .withCause(e)
                .build();
        }
    }

}
