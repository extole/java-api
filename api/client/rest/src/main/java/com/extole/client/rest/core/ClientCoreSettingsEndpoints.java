package com.extole.client.rest.core;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/settings/core")
public interface ClientCoreSettingsEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    ClientCoreSettingsResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientCoreSettingsResponse update(@UserAccessTokenParam String accessToken,
        ClientCoreSettingsRequest request)
        throws UserAuthorizationRestException, ClientCoreSettingsRestException;

}
