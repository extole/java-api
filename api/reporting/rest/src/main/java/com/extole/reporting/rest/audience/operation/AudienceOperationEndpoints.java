package com.extole.reporting.rest.audience.operation;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.api.audience.Audience;
import com.extole.api.audience.operation.AudienceOperation;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.id.Id;
import com.extole.reporting.rest.audience.operation.action.data.source.ActionAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.audience.operation.modification.data.source.PersonListAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.audience.operation.modification.data.source.ReportAudienceOperationDataSourceValidationRestException;

@Path("/v1/audiences/{audience_id}/operations")
@Tag(name = "/v1/audiences/{audience_id}/operations")
public interface AudienceOperationEndpoints {

    @GET
    @Path("/{operation_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get audience operation")
    AudienceOperationResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @PathParam("operation_id") Id<AudienceOperation> operationId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, AudienceOperationRestException;

    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "List audience operations")
    List<AudienceOperationResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @BeanParam AudienceOperationQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException;

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Create audience operation")
    AudienceOperationResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @RequestBody(required = true) AudienceOperationCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationValidationRestException, AudienceOperationRestException,
        PersonListAudienceOperationDataSourceValidationRestException,
        FileAssetAudienceOperationDataSourceValidationRestException,
        ReportAudienceOperationDataSourceValidationRestException,
        ActionAudienceOperationDataSourceValidationRestException;

    @POST
    @Path("/{operation_id}/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cancel audience operation")
    AudienceOperationStateResponse cancel(@UserAccessTokenParam String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @PathParam("operation_id") Id<AudienceOperation> operationId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException, CancelAudienceOperationRestException;

    @GET
    @Path("/{operation_id}/state")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get audience operation state")
    AudienceOperationStateResponse getState(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @PathParam("operation_id") Id<AudienceOperation> operationId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException;

    @GET
    @Path("/{operation_id}/state/debug")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get audience operation state")
    AudienceOperationStateDebugResponse getDebugState(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @PathParam("operation_id") Id<AudienceOperation> operationId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException;

    @GET
    @Path("/{operation_id}/view/details")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get audience operation with details")
    AudienceOperationDetailedResponse getWithDetails(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @PathParam("operation_id") Id<AudienceOperation> operationId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException;

    @POST
    @Path("/{operation_id}/retry")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retry audience operation")
    AudienceOperationResponse retry(@UserAccessTokenParam String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @PathParam("operation_id") Id<AudienceOperation> operationId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException, RetryAudienceOperationRestException;

}
