package com.extole.client.rest.audience;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.api.audience.Audience;
import com.extole.client.rest.audience.built.BuiltAudienceQueryParams;
import com.extole.client.rest.audience.built.BuiltAudienceResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.id.Id;

@Path("/v1/audiences")
@Tag(name = "/v1/audiences")
public interface AudienceEndpoints {

    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "List audiences")
    List<AudienceResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam AudienceQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{audience_id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Get audience")
    AudienceResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/built")
    @Operation(summary = "List built audiences")
    List<BuiltAudienceResponse> listBuilt(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam BuiltAudienceQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/{audience_id}/built")
    @Operation(summary = "Get built audience")
    BuiltAudienceResponse getBuilt(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceRestException;

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Create audience")
    AudienceResponse create(
        @UserAccessTokenParam String accessToken,
        @RequestBody(required = true) AudienceCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AudienceValidationRestException,
        CampaignComponentValidationRestException, BuildAudienceRestException;

    @PUT
    @Path("/{audience_id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Update audience")
    AudienceResponse update(@UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("audience_id") Id<Audience> audienceId,
        @RequestBody(required = true) AudienceUpdateRequest updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceRestException, AudienceValidationRestException,
        OmissibleRestException, CampaignComponentValidationRestException, BuildAudienceRestException;

    @DELETE
    @Path("/{audience_id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Archive audience")
    AudienceResponse archive(@UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("audience_id") Id<Audience> audienceId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceRestException, AudienceArchiveRestException;

    @POST
    @Path("/{audience_id}/delete")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Delete audience")
    AudienceResponse delete(@UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("audience_id") Id<Audience> audienceId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceRestException, AudienceArchiveRestException;

    @POST
    @Path("/{audience_id}/unarchive")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Unarchive audience")
    AudienceResponse unArchive(@UserAccessTokenParam String accessToken,
        @Parameter(required = true) @PathParam("audience_id") Id<Audience> audienceId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceRestException, AudienceArchiveRestException;

}
