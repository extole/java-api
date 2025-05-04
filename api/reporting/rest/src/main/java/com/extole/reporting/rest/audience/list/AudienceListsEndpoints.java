package com.extole.reporting.rest.audience.list;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.reporting.rest.audience.list.request.AudienceListRequest;
import com.extole.reporting.rest.audience.list.response.AudienceListDebugResponse;
import com.extole.reporting.rest.audience.list.response.AudienceListResponse;

@Path("/v6/audience-lists")
@Tag(name = "/v6/audience-lists", description = "AudienceList")
public interface AudienceListsEndpoints {

    String AUDIENCE_ID_PATH_PARAM_NAME = "audienceId";

    @GET
    @Path("/{audienceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets AudienceList details")
    <T extends AudienceListResponse> T get(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The AudienceList unique identifier",
            required = true) @PathParam(AUDIENCE_ID_PATH_PARAM_NAME) String audienceId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AudienceListRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lists AudienceList details")
    <T extends AudienceListResponse> List<T> list(
        @UserAccessTokenParam String accessToken,
        @BeanParam AudienceListQueryParams audienceListRequest,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AudienceListRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a new AudienceList")
    <T extends AudienceListResponse> T create(
        @UserAccessTokenParam String accessToken,
        AudienceListRequest request,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceListValidationRestException,
        DynamicAudienceListValidationRestException, StaticAudienceListValidationRestException,
        AudienceListRestException, UploadedAudienceListValidationRestException;

    @PUT
    @Path("/{" + AUDIENCE_ID_PATH_PARAM_NAME + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Updates an existing AudienceList")
    <T extends AudienceListResponse> T update(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The AudienceList unique identifier",
            required = true) @PathParam(AUDIENCE_ID_PATH_PARAM_NAME) String audienceId,
        AudienceListRequest request,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceListRestException, AudienceListValidationRestException,
        DynamicAudienceListValidationRestException, StaticAudienceListValidationRestException,
        UploadedAudienceListValidationRestException;

    @POST
    @Path("/{" + AUDIENCE_ID_PATH_PARAM_NAME + "}/archive")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Archives an AudienceList", description = "Returns the archived AudienceList")
    <T extends AudienceListResponse> T archive(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The AudienceList unique identifier",
            required = true) @PathParam(AUDIENCE_ID_PATH_PARAM_NAME) String audienceId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AudienceListRestException;

    @GET
    @Path("/{" + AUDIENCE_ID_PATH_PARAM_NAME + "}/download{format :(\\.csv|\\.json|\\.jsonl|\\.psv|\\.xlsx|\\.psv)?}")
    @Operation(summary = "Returns AudienceList content in specified format")
    Response download(@AccessTokenParam String accessToken,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @Parameter(description = "The AudienceList unique identifier",
            required = true) @PathParam(AUDIENCE_ID_PATH_PARAM_NAME) String audienceId,
        @Parameter(description = "Format parameter") @PathParam("format") Optional<String> format,
        @Parameter(description = "Limit parameter") @QueryParam("limit") Optional<Integer> limit,
        @Parameter(description = "Offset parameter") @QueryParam("offset") Optional<Integer> offset,
        @Nullable @TimeZoneParam ZoneId timeZone) throws AudienceListRestException, UserAuthorizationRestException;

    @POST
    @Path("/{" + AUDIENCE_ID_PATH_PARAM_NAME + "}/snapshot")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a snapshot for the AudienceList")
    <T extends AudienceListResponse> T snapshot(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The AudienceList unique identifier",
            required = true) @PathParam(AUDIENCE_ID_PATH_PARAM_NAME) String audienceId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AudienceListRestException;

    @POST
    @Path("/{" + AUDIENCE_ID_PATH_PARAM_NAME + "}/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Refreshes the AudienceList, in case of DynamicAudienceList it is re-executed")
    <T extends AudienceListResponse> T refresh(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The AudienceList unique identifier",
            required = true) @PathParam(AUDIENCE_ID_PATH_PARAM_NAME) String audienceId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AudienceListRestException;

    @GET
    @Path("/{" + AUDIENCE_ID_PATH_PARAM_NAME + "}/debug")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get AudienceList Debug Information")
    AudienceListDebugResponse readDebug(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @Parameter(description = "The AudienceList unique identifier",
            required = true) @PathParam(AUDIENCE_ID_PATH_PARAM_NAME) String audienceId)
        throws UserAuthorizationRestException, AudienceListRestException;

    @POST
    @Path("/{" + AUDIENCE_ID_PATH_PARAM_NAME + "}/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cancel the AudienceList")
    <T extends AudienceListResponse> T cancel(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The AudienceList unique identifier",
            required = true) @PathParam(AUDIENCE_ID_PATH_PARAM_NAME) String audienceId,
        @Nullable @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AudienceListRestException;
}
