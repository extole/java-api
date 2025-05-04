package com.extole.client.rest.auth.provider.type.openid.connect;

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

import com.extole.client.rest.auth.provider.type.AuthProviderTypeValidationRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v1/auth-provider-types/openid-connect")
public interface OpenIdConnectAuthProviderTypeEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    List<OpenIdConnectAuthProviderTypeResponse> listOpenIdConnectAuthProviderTypes(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{auth_provider_type_id}")
    OpenIdConnectAuthProviderTypeResponse getOpenIdConnectAuthProviderType(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("auth_provider_type_id") String authProviderTypeId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, OpenIdConnectAuthProviderTypeRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @POST
    OpenIdConnectAuthProviderTypeResponse createOpenIdConnectAuthProviderType(@UserAccessTokenParam String accessToken,
        OpenIdConnectAuthProviderTypeCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderTypeValidationRestException,
        OpenIdConnectAuthProviderTypeValidationRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @PUT
    @Path("/{auth_provider_type_id}")
    OpenIdConnectAuthProviderTypeResponse updateOpenIdConnectAuthProviderType(@UserAccessTokenParam String accessToken,
        @PathParam("auth_provider_type_id") String authProviderTypeId,
        OpenIdConnectAuthProviderTypeUpdateRequest updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, OpenIdConnectAuthProviderTypeRestException,
        AuthProviderTypeValidationRestException, OpenIdConnectAuthProviderTypeValidationRestException;

    @Produces(APPLICATION_JSON)
    @DELETE
    @Path("/{auth_provider_type_id}")
    OpenIdConnectAuthProviderTypeResponse archiveOpenIdConnectAuthProviderType(@UserAccessTokenParam String accessToken,
        @PathParam("auth_provider_type_id") String authProviderTypeId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, OpenIdConnectAuthProviderTypeRestException;
}
