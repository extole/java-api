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

@Path(PiiObfuscateFixupTransformationEndpoints.FIXUPS_URI)
public interface PiiObfuscateFixupTransformationEndpoints {

    String FIXUPS_URI = "/v2/fixups";

    @GET
    @Path("/{fixupId}/transformations/pii_obfuscate/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    PiiObfuscateFixupTransformationResponse getTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException;

    @POST
    @Path("/{fixupId}/transformations/pii_obfuscate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    PiiObfuscateFixupTransformationResponse createTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        PiiFixupTransformationRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationValidationRestException;

    @PUT
    @Path("/{fixupId}/transformations/pii_obfuscate/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    PiiObfuscateFixupTransformationResponse updateTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId,
        PiiFixupTransformationRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException;

    @DELETE
    @Path("/{fixupId}/transformations/pii_obfuscate/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    PiiObfuscateFixupTransformationResponse deleteTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException;
}
