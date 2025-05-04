package com.extole.client.rest.auth.provider.type;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v1/auth-provider-types")
public interface AuthProviderTypeEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    List<AuthProviderTypeResponse> listAuthProviderTypes(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("protocol") String authProviderTypeProtocol,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderTypeQueryRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{auth_provider_type_id}")
    AuthProviderTypeResponse getAuthProviderType(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("auth_provider_type_id") String authProviderTypeId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderTypeRestException;

}
