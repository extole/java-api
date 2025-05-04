package com.extole.client.rest.auth.provider.type.extole;

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

@Path("/v1/auth-provider-types/extole")
public interface ExtoleAuthProviderTypeEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    List<ExtoleAuthProviderTypeResponse> listExtoleAuthProviderTypes(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{auth_provider_type_id}")
    ExtoleAuthProviderTypeResponse getExtoleAuthProviderType(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("auth_provider_type_id") String authProviderTypeId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ExtoleAuthProviderTypeRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @POST
    ExtoleAuthProviderTypeResponse createExtoleAuthProviderType(@UserAccessTokenParam String accessToken,
        ExtoleAuthProviderTypeCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AuthProviderTypeValidationRestException;

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @PUT
    @Path("/{auth_provider_type_id}")
    ExtoleAuthProviderTypeResponse updateExtoleAuthProviderType(@UserAccessTokenParam String accessToken,
        @PathParam("auth_provider_type_id") String authProviderTypeId,
        ExtoleAuthProviderTypeUpdateRequest updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ExtoleAuthProviderTypeRestException,
        AuthProviderTypeValidationRestException;

    @Produces(APPLICATION_JSON)
    @DELETE
    @Path("/{auth_provider_type_id}")
    ExtoleAuthProviderTypeResponse archiveExtoleAuthProviderType(@UserAccessTokenParam String accessToken,
        @PathParam("auth_provider_type_id") String authProviderTypeId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ExtoleAuthProviderTypeRestException;
}
