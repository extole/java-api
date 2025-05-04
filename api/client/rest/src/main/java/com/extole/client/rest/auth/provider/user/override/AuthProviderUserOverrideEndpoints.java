package com.extole.client.rest.auth.provider.user.override;

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

import com.extole.client.rest.auth.provider.AuthProviderRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/settings/auth-providers/{auth_provider_id}/user-overrides")
public interface AuthProviderUserOverrideEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    List<AuthProviderUserOverrideResponse> listAuthProviderUserOverrides(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("auth_provider_id") String authProviderId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{override_id}")
    AuthProviderUserOverrideResponse getAuthProviderUserOverride(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("auth_provider_id") String authProviderId,
        @PathParam("override_id") String overrideId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderRestException, AuthProviderUserOverrideRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @POST
    AuthProviderUserOverrideResponse createAuthProviderUserOverride(@UserAccessTokenParam String accessToken,
        @PathParam("auth_provider_id") String authProviderId,
        AuthProviderUserOverrideCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderRestException,
        AuthProviderUserOverrideValidationRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @PUT
    @Path("/{override_id}")
    AuthProviderUserOverrideResponse updateAuthProviderUserOverride(@UserAccessTokenParam String accessToken,
        @PathParam("auth_provider_id") String authProviderId,
        @PathParam("override_id") String overrideId,
        AuthProviderUserOverrideUpdateRequest updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderRestException, AuthProviderUserOverrideRestException,
        AuthProviderUserOverrideValidationRestException;

    @Produces(APPLICATION_JSON)
    @DELETE
    @Path("/{override_id}")
    AuthProviderUserOverrideResponse archiveAuthProviderUserOverride(@UserAccessTokenParam String accessToken,
        @PathParam("auth_provider_id") String authProviderId,
        @PathParam("override_id") String overrideId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderRestException, AuthProviderUserOverrideRestException;
}
