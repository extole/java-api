package com.extole.client.rest.impl.core;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.core.CoreVersionsEndpoints;
import com.extole.client.rest.core.CoreVersionsResponse;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.service.client.core.CoreVersionsException;
import com.extole.model.service.client.core.CoreVersionsService;

@Provider
public class CoreVersionsEndpointsImpl implements CoreVersionsEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final CoreVersionsService coreVersionsService;

    @Autowired
    public CoreVersionsEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        CoreVersionsService coreVersionsService) {
        this.authorizationProvider = authorizationProvider;
        this.coreVersionsService = coreVersionsService;
    }

    @Override
    public CoreVersionsResponse getVersions(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        // TODO check if we should move the token validation to service layer ENG-20365
        if (!authorization.isAuthorized(authorization.getClientId(), Authorization.Scope.USER_SUPPORT)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .addParameter("client_id", authorization.getClientId()).build();
        }
        try {
            return new CoreVersionsResponse(this.coreVersionsService.getAvailableCoreVersions());
        } catch (CoreVersionsException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }
}
