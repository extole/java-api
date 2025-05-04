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

@Path("/v2/default/properties")
public interface DefaultPropertyEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PropertyResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    PropertyResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("name") String name)
        throws PropertyRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PropertyResponse create(@UserAccessTokenParam String accessToken, PropertyCreationRequest property)
        throws UserAuthorizationRestException, PropertyCreationRestException,
        PropertyValidationRestException, PropertyRestException;

    @PUT
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    PropertyResponse update(@UserAccessTokenParam String accessToken, @PathParam("name") String name,
        PropertyUpdateRequest property)
        throws UserAuthorizationRestException, PropertyRestException, PropertyValidationRestException,
        PropertyUpdateRestException;

    @DELETE
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    SuccessResponse delete(@UserAccessTokenParam String accessToken, @PathParam("name") String name)
        throws UserAuthorizationRestException, PropertyRestException;

}
