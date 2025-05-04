package com.extole.client.rest.optout;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v4/optouts")
public interface OptoutEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    OptoutResponse isOptout(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("email") String email)
        throws UserAuthorizationRestException, OptoutRestException;
}
