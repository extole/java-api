package com.extole.client.rest.auth.provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.extole.client.rest.auth.provider.type.AuthProviderTypeRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/settings/auth-providers")
public interface AuthProviderEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    List<AuthProviderResponse> listAuthProviders(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{auth_provider_id}")
    AuthProviderResponse getAuthProvider(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("auth_provider_id") String authProviderId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AuthProviderRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @POST
    AuthProviderResponse createAuthProvider(@UserAccessTokenParam String accessToken,
        AuthProviderCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AuthProviderTypeRestException,
        AuthProviderValidationRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @PUT
    @Path("/{auth_provider_id}")
    AuthProviderResponse updateAuthProvider(@UserAccessTokenParam String accessToken,
        @PathParam("auth_provider_id") String authProviderId,
        AuthProviderUpdateRequest updateRequest,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AuthProviderRestException,
        AuthProviderTypeRestException, AuthProviderValidationRestException;

    @Produces(APPLICATION_JSON)
    @DELETE
    @Path("/{auth_provider_id}")
    AuthProviderResponse archiveAuthProvider(@UserAccessTokenParam String accessToken,
        @PathParam("auth_provider_id") String authProviderId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AuthProviderRestException;
}
