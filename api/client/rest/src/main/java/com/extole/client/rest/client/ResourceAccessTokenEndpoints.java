package com.extole.client.rest.client;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v4/tokens/resource")
public interface ResourceAccessTokenEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    ResourceAccessTokenResponse getToken(
        @UserAccessTokenParam(requiredScope = Scope.ANY) String accessToken)
        throws UserAuthorizationRestException;

    @DELETE
    @Path("/{access_token_to_delete}")
    void delete(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("access_token_to_delete") String accessTokenToDelete)
        throws ManagedAccessTokenRestException, UserAuthorizationRestException, ResourceAccessTokenRestException;
}
