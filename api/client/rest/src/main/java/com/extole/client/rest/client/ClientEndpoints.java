package com.extole.client.rest.client;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path(ClientEndpoints.CLIENT_PATH)
@Tag(name = ClientEndpoints.CLIENT_PATH, description = "Client")
public interface ClientEndpoints {

    String CLIENT_PATH = "/v4/clients";

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a new client")
    ClientResponse create(@UserAccessTokenParam(required = false, requiredScope = Scope.ANY) String accessToken,
        ClientCreationRequest request) throws ClientValidationRestException, UserAuthorizationRestException;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns a client")
    List<ClientResponse> clients(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientResponse client(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("clientId") String clientId) throws ClientRestException, UserAuthorizationRestException;

    @DELETE
    @Path("/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientResponse delete(@UserAccessTokenParam String accessToken, @PathParam("clientId") String clientId)
        throws UserAuthorizationRestException;

    @PUT
    @Path("/{clientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Used to update client")
    ClientResponse update(@UserAccessTokenParam String accessToken, @PathParam("clientId") String clientId,
        ClientUpdateRequest request) throws ClientValidationRestException, UserAuthorizationRestException;

    /**
     * Undeletes a client by restoring the client and the program domains that were active at the moment of client
     * deletion.
     * <p>
     * A client can be undeleted only by an Extole SUPER_USER.
     * <p>
     * <b>WARNING!!!</b> The method does not restore client users and tokens.
     *
     * @param accessToken SUPER_USER access token for Extole client
     * @param clientId id of the client to be undeleted
     * @return the undeleted client
     * @throws UserAuthorizationRestException if the user is not authorized to undelete the client
     */
    @POST
    @Path("/undelete/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientResponse undelete(@UserAccessTokenParam String accessToken, @PathParam("clientId") String clientId)
        throws UserAuthorizationRestException;
}
