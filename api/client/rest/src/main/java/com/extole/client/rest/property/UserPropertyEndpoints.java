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

import com.extole.client.rest.user.UserRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;

@Path("/v2/users/{user_id}/properties")
public interface UserPropertyEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PropertyResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("user_id") String userId) throws UserAuthorizationRestException, UserRestException;

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    PropertyResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("user_id") String userId, @PathParam("name") String name)
        throws UserAuthorizationRestException, UserRestException, PropertyRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PropertyResponse create(@UserAccessTokenParam String accessToken, @PathParam("user_id") String userId,
        PropertyCreationRequest property) throws UserAuthorizationRestException, UserRestException,
        PropertyRestException, PropertyCreationRestException, PropertyValidationRestException;

    @PUT
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    PropertyResponse update(@UserAccessTokenParam String accessToken, @PathParam("user_id") String userId,
        @PathParam("name") String name, PropertyUpdateRequest property) throws UserAuthorizationRestException,
        UserRestException, PropertyRestException, PropertyValidationRestException, PropertyUpdateRestException;

    @DELETE
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    SuccessResponse delete(@UserAccessTokenParam String accessToken, @PathParam("user_id") String userId,
        @PathParam("name") String name)
        throws UserAuthorizationRestException, UserRestException, PropertyRestException;

}
