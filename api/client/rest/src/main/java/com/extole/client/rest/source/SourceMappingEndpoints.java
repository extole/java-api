package com.extole.client.rest.source;

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

@Path("/v2/source-mapping")
public interface SourceMappingEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<SourceMappingResponse> get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{sourceMappingId}")
    @Produces(MediaType.APPLICATION_JSON)
    SourceMappingResponse getById(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("sourceMappingId") String sourceMappingId)
        throws UserAuthorizationRestException, SourceMappingRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SourceMappingResponse create(@UserAccessTokenParam String accessToken, SourceMappingRequest request)
        throws UserAuthorizationRestException, SourceMappingValidationRestException;

    @PUT
    @Path("/{sourceMappingId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SourceMappingResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("sourceMappingId") String sourceMappingId, SourceMappingRequest request)
        throws UserAuthorizationRestException, SourceMappingValidationRestException, SourceMappingRestException;

    @DELETE
    @Path("/{sourceMappingId}")
    @Produces(MediaType.APPLICATION_JSON)
    SourceMappingResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("sourceMappingId") String sourceMappingId)
        throws UserAuthorizationRestException, SourceMappingRestException;

}
