package com.extole.client.rest.client;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.client.rest.client.core.AuthCodeResponseValidateRequest;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path(ClientAccessTokenEndpoints.CLIENT_PATH)
@Tag(name = ClientAccessTokenEndpoints.CLIENT_PATH, description = "ClientAccessToken")
public interface ClientAccessTokenEndpoints {

    String CLIENT_PATH = "/v4/tokens";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Used to create a new access token")
    AccessTokenResponse create(@UserAccessTokenParam(required = false, requiredScope = Scope.ANY) String accessToken,
        Optional<AccessTokenCreationRequest> creationRequest)
        throws AccessTokenCreationRestException, UserAuthorizationRestException;

    @POST
    @Path("/openid-connect/authorization-code-flow")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Used to validate an existing access token")
    AccessTokenResponse validate(AuthCodeResponseValidateRequest authCodeResponseValidateRequest,
        @HeaderParam("X-CSRF-TOKEN") String csrfToken, @HeaderParam("X-NONCE") String nonce)
        throws AccessTokenAuthCodeResponseValidateRestException, AccessTokenCreationRestException;

    @GET
    @Path("/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    AccessTokenResponse getToken(@PathParam("token") String accessToken) throws UserAuthorizationRestException;

    @GET
    @Path("/{token}/debug")
    @Produces(MediaType.APPLICATION_JSON)
    @Hidden
    AccessTokenResponse getToken(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String authorizingAccessToken,
        @PathParam("token") String accessToken) throws UserAuthorizationRestException, ClientAccessTokenRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Used to get access token details")
    AccessTokenResponse get(@UserAccessTokenParam(requiredScope = Scope.ANY) String accessToken)
        throws UserAuthorizationRestException;

    @DELETE
    @Path("/{token}")
    @Operation(description = "Used to invalidate an existing access token")
    void delete(@PathParam("token") String accessToken) throws UserAuthorizationRestException;

    @PUT // TODO https://extole.atlassian.net/browse/ENG-8454
    @Path("/exchange/{token}")
    @Operation(description = "Used to change existing access token to a new one")
    AccessTokenResponse exchange(@PathParam("token") String accessToken) throws UserAuthorizationRestException;
}
