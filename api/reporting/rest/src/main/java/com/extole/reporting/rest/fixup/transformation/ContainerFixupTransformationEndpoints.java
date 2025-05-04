package com.extole.reporting.rest.fixup.transformation;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.reporting.rest.fixup.FixupRestException;

@Path(ContainerFixupTransformationEndpoints.FIXUPS_URI)
public interface ContainerFixupTransformationEndpoints {

    String FIXUPS_URI = "/v2/fixups";

    @GET
    @Path("/{fixupId}/transformations/container/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    ContainerFixupTransformationResponse getTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException;

    @POST
    @Path("/{fixupId}/transformations/container")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ContainerFixupTransformationResponse createTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        ContainerFixupTransformationRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationValidationRestException,
        ContainerFixupTransformationValidationRestException;

    @PUT
    @Path("/{fixupId}/transformations/container/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ContainerFixupTransformationResponse updateTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId,
        ContainerFixupTransformationRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException,
        ContainerFixupTransformationValidationRestException;

    @DELETE
    @Path("/{fixupId}/transformations/container/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    ContainerFixupTransformationResponse deleteTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException;
}
