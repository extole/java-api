package com.extole.client.rest.client;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v4/tokens/managed")
public interface ManagedAccessTokenEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ManagedAccessTokenResponse createManagedToken(@UserAccessTokenParam String accessToken,
        Optional<ManagedAccessTokenCreationRequest> request, @TimeZoneParam ZoneId timeZone)
        throws ManagedAccessTokenRestException, UserAuthorizationRestException;

    @POST
    @Path("/openid-connect/authorization-code-flow")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ManagedAccessTokenResponse createManagedToken(@UserAccessTokenParam String accessToken,
        AuthCodeManagedAccessTokenCreationRequest authCodeManagedAccessTokenCreationRequest,
        @HeaderParam("X-CSRF-TOKEN") String csrfToken, @HeaderParam("X-NONCE") String nonce,
        @TimeZoneParam ZoneId timeZone) throws ManagedAccessTokenRestException, UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ManagedAccessTokenResponse> getTokens(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @DELETE
    @Path("/{access_token_id}")
    void delete(@UserAccessTokenParam String accessToken, @PathParam("access_token_id") String accessTokenId,
        @TimeZoneParam ZoneId timeZone) throws ManagedAccessTokenRestException, UserAuthorizationRestException;
}
