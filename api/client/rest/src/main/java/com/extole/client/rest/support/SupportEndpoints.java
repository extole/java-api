package com.extole.client.rest.support;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v4/support")
public interface SupportEndpoints {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    SupportResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SupportResponse update(@UserAccessTokenParam String accessToken, SupportRequest request)
        throws UserAuthorizationRestException, SupportValidationRestException, SupportRestException;
}
