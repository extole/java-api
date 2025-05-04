package com.extole.client.rest.property;

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

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;

@Path("/v2/properties")
public interface ClientPropertyEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ClientPropertyResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientPropertyResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("name") String name)
        throws UserAuthorizationRestException, PropertyRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientPropertyResponse create(@UserAccessTokenParam String accessToken, ClientPropertyCreationRequest property)
        throws UserAuthorizationRestException, PropertyValidationRestException, PropertyCreationRestException,
        PropertyRestException;

    @PUT
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ClientPropertyResponse update(@UserAccessTokenParam String accessToken, @PathParam("name") String name,
        ClientPropertyUpdateRequest property) throws UserAuthorizationRestException, PropertyRestException,
        PropertyUpdateRestException, PropertyValidationRestException;

    @DELETE
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    SuccessResponse delete(@UserAccessTokenParam String accessToken, @PathParam("name") String name)
        throws UserAuthorizationRestException, PropertyRestException;

}
