package com.extole.client.rest.client;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v4/oauth/tokens")
public interface OAuthClientCredentialsAccessTokenEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    OAuthAccessTokenResponse create(
        @UserAccessTokenParam(required = false, requiredScope = Scope.ANY) String credentials,
        @BeanParam OAuthClientCredentialsRequestParameters requestParameters)
        throws OAuthClientCredentialsAccessTokenRestException, UserAuthorizationRestException,
        AccessTokenCreationRestException;
}
