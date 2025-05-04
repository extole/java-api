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

@Path(ConditionalAliasChangeFixupTransformationEndpoints.FIXUPS_URI)
public interface ConditionalAliasChangeFixupTransformationEndpoints {

    String FIXUPS_URI = "/v2/fixups";

    @GET
    @Path("/{fixupId}/transformations/conditional_alias_change/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    ConditionalAliasChangeFixupTransformationResponse getTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException;

    @POST
    @Path("/{fixupId}/transformations/conditional_alias_change")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ConditionalAliasChangeFixupTransformationResponse createTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        ConditionalAliasChangeFixupTransformationCreateRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationValidationRestException,
        ConditionalAliasChangeFixupTransformationValidationRestException;

    @PUT
    @Path("/{fixupId}/transformations/conditional_alias_change/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ConditionalAliasChangeFixupTransformationResponse updateTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId,
        ConditionalAliasChangeFixupTransformationUpdateRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException,
        FixupTransformationValidationRestException, ConditionalAliasChangeFixupTransformationValidationRestException;

    @DELETE
    @Path("/{fixupId}/transformations/conditional_alias_change/{transformationId}")
    @Produces(MediaType.APPLICATION_JSON)
    ConditionalAliasChangeFixupTransformationResponse deleteTransformation(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("transformationId") String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException;
}
