package com.extole.client.rest.dimension;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/dimension-mappings")
public interface DimensionMappingEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<DimensionMappingResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("dimension") String dimension)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{dimensionMappingId}")
    @Produces(MediaType.APPLICATION_JSON)
    DimensionMappingResponse getById(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("dimensionMappingId") String dimensionMappingId)
        throws UserAuthorizationRestException, DimensionMappingRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    DimensionMappingResponse create(@UserAccessTokenParam String accessToken, DimensionMappingRequest request)
        throws UserAuthorizationRestException, DimensionMappingValidationRestException;

    @PUT
    @Path("/{dimensionMappingId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    DimensionMappingResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("dimensionMappingId") String dimensionMappingId, DimensionMappingRequest request)
        throws UserAuthorizationRestException, DimensionMappingValidationRestException, DimensionMappingRestException;

    @DELETE
    @Path("/{dimensionMappingId}")
    @Produces(MediaType.APPLICATION_JSON)
    DimensionMappingResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("dimensionMappingId") String dimensionMappingId)
        throws UserAuthorizationRestException, DimensionMappingRestException;

}
