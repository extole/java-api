package com.extole.client.rest.security.exchange;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v6/security/oauth/flow")
public interface OAuthCodeExchangeFlowEndpoints {

    @POST
    @Path("/exchange")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    OAuthFlowCodeExchangeResponse exchange(
        @UserAccessTokenParam(required = false, requiredScope = Scope.ANY) String accessToken,
        OAuthFlowCodeExchangeRequest request) throws UserAuthorizationRestException, OAuthCodeExchangeFlowRestException;
}
