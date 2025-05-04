package com.extole.client.rest.prehandler;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.prehandler.built.BuiltPrehandlerResponse;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/prehandlers")
@Tag(name = "/v6/prehandlers", description = "Prehandler")
public interface PrehandlerEndpoints {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "List existing prehandlers",
        description = "Returns the existing prehandlers, sorted by their execution order.")
    List<PrehandlerResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam PrehandlerListQueryParams requestParams,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/built")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "List existing built prehandlers",
        description = "Returns the existing built prehandlers, sorted by their execution order.")
    List<BuiltPrehandlerResponse> listBuilt(@UserAccessTokenParam String accessToken,
        @BeanParam PrehandlerListQueryParams requestParams, @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{prehandlerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve a prehandler", description = "Retrieves an existing prehandler.")
    PrehandlerResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The id of the prehandler to be retrieved.",
            required = true) @PathParam("prehandlerId") String prehandlerId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PrehandlerRestException;

    @GET
    @Path("/{prehandlerId}/built")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve a built prehandler", description = "Retrieves an existing built prehandler.")
    BuiltPrehandlerResponse getBuilt(@UserAccessTokenParam String accessToken,
        @Parameter(description = "The id of the prehandler to be retrieved.",
            required = true) @PathParam("prehandlerId") String prehandlerId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PrehandlerRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create new prehandler", description = "Creates a new prehandler.")
    PrehandlerResponse create(
        @UserAccessTokenParam String accessToken,
        @RequestBody(description = "PrehandlerCreateRequest object", required = true) PrehandlerCreateRequest request,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildPrehandlerRestException,
        PrehandlerConditionValidationRestException, PrehandlerActionValidationRestException,
        CampaignComponentValidationRestException, OmissibleRestException;

    @PUT
    @Path("/{prehandlerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update prehandler", description = "Updates an existing prehandler.")
    PrehandlerResponse update(@UserAccessTokenParam String accessToken,
        @Parameter(description = "The id of the prehandler to be updated.",
            required = true) @PathParam("prehandlerId") String prehandlerId,
        @RequestBody(description = "PrehandlerUpdateRequest object",
            required = true) PrehandlerUpdateRequest request,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PrehandlerRestException, BuildPrehandlerRestException,
        PrehandlerConditionValidationRestException, PrehandlerActionValidationRestException,
        CampaignComponentValidationRestException, OmissibleRestException;

    @POST
    @Path("/{prehandlerId}/archive")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Archive prehandler")
    PrehandlerResponse archive(@UserAccessTokenParam String accessToken,
        @Parameter(description = "The id of the prehandler to be archived.",
            required = true) @PathParam("prehandlerId") String prehandlerId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PrehandlerRestException;

    @POST
    @Path("/{prehandlerId}/unarchive")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Unarchive prehandler")
    PrehandlerResponse unArchive(@UserAccessTokenParam String accessToken,
        @Parameter(description = "The id of the prehandler to be unarchived.",
            required = true) @PathParam("prehandlerId") String prehandlerId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PrehandlerRestException;

    @DELETE
    @Path("/{prehandlerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete prehandler")
    PrehandlerResponse delete(@UserAccessTokenParam String accessToken,
        @Parameter(description = "The id of the prehandler to be deleted.",
            required = true) @PathParam("prehandlerId") String prehandlerId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PrehandlerRestException;
}
