package com.extole.client.rest.auth.method;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v1/auth-methods")
public interface AuthMethodEndpoints {

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @POST
    @Path("/discover")
    List<AuthMethodResponse> discoverAuthMethods(Optional<AuthMethodDiscoverRequest> discoverRequest);

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @POST
    @Path("/discover/re-auth")
    List<AuthMethodResponse>
        discoverReAuthMethods(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
            throws UserAuthorizationRestException;

}
