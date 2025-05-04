package com.extole.client.rest.impl.auth.provider.type;

import static com.extole.model.service.auth.provider.type.AuthProviderTypeQueryBuilder.AuthProviderTypeProtocol.ANY;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.auth.provider.type.AuthProviderTypeEndpoints;
import com.extole.client.rest.auth.provider.type.AuthProviderTypeQueryRestException;
import com.extole.client.rest.auth.provider.type.AuthProviderTypeResponse;
import com.extole.client.rest.auth.provider.type.AuthProviderTypeRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.auth.provider.type.AuthProviderType;
import com.extole.model.service.auth.provider.type.AuthProviderTypeNotFoundException;
import com.extole.model.service.auth.provider.type.AuthProviderTypeQueryBuilder;
import com.extole.model.service.auth.provider.type.AuthProviderTypeService;

@Provider
public class AuthProviderTypeEndpointsImpl implements AuthProviderTypeEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final AuthProviderTypeService authProviderTypeService;
    private final AuthProviderTypeRestMapper authProviderTypeRestMapper;

    @Autowired
    public AuthProviderTypeEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        AuthProviderTypeService authProviderTypeService,
        AuthProviderTypeRestMapper authProviderTypeRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.authProviderTypeService = authProviderTypeService;
        this.authProviderTypeRestMapper = authProviderTypeRestMapper;
    }

    @Override
    public List<AuthProviderTypeResponse> listAuthProviderTypes(String accessToken, String authProviderTypeProtocol,
        ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderTypeQueryRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return authProviderTypeService.createAuthProviderTypeQueryBuilder(authorization)
                .withAuthProviderTypeProtocol(mapAuthProviderTypeProtocol(authProviderTypeProtocol))
                .list()
                .stream()
                .map(AuthProviderType -> authProviderTypeRestMapper.toAuthProviderTypeResponse(AuthProviderType,
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
    public AuthProviderTypeResponse getAuthProviderType(String accessToken, String authProviderTypeId, ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderTypeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            AuthProviderType authProviderType =
                authProviderTypeService.getAuthProviderType(authorization, Id.valueOf(authProviderTypeId));
            return authProviderTypeRestMapper.toAuthProviderTypeResponse(authProviderType, timeZone);
        } catch (AuthProviderTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeRestException.class)
                .withErrorCode(AuthProviderTypeRestException.AUTH_PROVIDER_TYPE_NOT_FOUND)
                .addParameter("auth_provider_type_id", authProviderTypeId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private AuthProviderTypeQueryBuilder.AuthProviderTypeProtocol
        mapAuthProviderTypeProtocol(String authProviderTypeProtocol)
            throws AuthProviderTypeQueryRestException {
        try {
            return authProviderTypeProtocol == null ? ANY
                : AuthProviderTypeQueryBuilder.AuthProviderTypeProtocol.valueOf(authProviderTypeProtocol.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(AuthProviderTypeQueryRestException.class)
                .withErrorCode(AuthProviderTypeQueryRestException.AUTH_PROVIDER_TYPE_UNKNOWN_PROTOCOL)
                .addParameter("auth_provider_type_protocol", authProviderTypeProtocol)
                .withCause(e)
                .build();
        }

    }

}
