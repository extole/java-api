package com.extole.client.rest.security;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/settings/security")
public interface ClientSecuritySettingsEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    ClientSecuritySettingsResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientSecuritySettingsResponse update(@UserAccessTokenParam String accessToken,
        ClientSecuritySettingsRequest request)
        throws UserAuthorizationRestException, ClientSecuritySettingsRestException;
}
