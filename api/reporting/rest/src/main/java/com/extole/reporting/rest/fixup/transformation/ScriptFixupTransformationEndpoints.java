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

@Path(ScriptFixupTransformationEndpoints.FIXUPS_URI)
public interface ScriptFixupTransformationEndpoints {

    String FIXUPS_URI = "/v2/fixups";

    @GET
    @Path("/{fixupId}/transformations/script/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    ScriptFixupTransformationResponse getTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException;

    @POST
    @Path("/{fixupId}/transformations/script")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ScriptFixupTransformationResponse createTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        ScriptFixupTransformationRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationValidationRestException,
        ScriptFixupTransformationValidationRestException;

    @PUT
    @Path("/{fixupId}/transformations/script/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ScriptFixupTransformationResponse updateTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId,
        ScriptFixupTransformationRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException,
        ScriptFixupTransformationValidationRestException;

    @DELETE
    @Path("/{fixupId}/transformations/script/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    ScriptFixupTransformationResponse deleteTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException;
}
