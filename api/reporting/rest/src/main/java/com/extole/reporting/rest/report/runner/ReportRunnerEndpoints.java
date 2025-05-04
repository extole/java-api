package com.extole.reporting.rest.report.runner;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.reporting.rest.report.execution.ReportDownloadRestException;
import com.extole.reporting.rest.report.execution.ReportNotFoundRestException;
import com.extole.reporting.rest.report.execution.ReportResponse;
import com.extole.reporting.rest.report.execution.ReportRestException;
import com.extole.reporting.rest.report.execution.ReportValidationRestException;

@Path("/v6/report-runners")
@Tag(name = "/v6/report-runners", description = "ReportRunner")
public interface ReportRunnerEndpoints {

    String REPORT_RUNNER_ID_PATH_PARAM_NAME = "reportRunnerId";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a filtered list of report runners")
    List<? extends ReportRunnerResponse> getReportRunners(
        @UserAccessTokenParam String accessToken,
        @Nullable @BeanParam ReportRunnersListRequest reportRunnersListRequest)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRunnerQueryRestException;

    @GET
    @Path("/{reportRunnerId}/reports")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a filtered list of reports executed by the report runner." +
        "Results are sorted by schedule date in descending order.")
    List<BaseReportRunnerReportResponse> getReports(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The Extole unique report runner identifier.") @PathParam("reportRunnerId")
        String reportRunnerId,
        @Parameter(description = "Optional filter for types, " +
            "will return reports that have one of the specified values. " +
            "Valid values: EXECUTED, NOT_EXECUTED, ROLLING, INDIVIDUAL, EMPTY_SLOT, ACCUMULATING")
        @Nullable
        @QueryParam("types")
        String types,
        @Parameter(description = "Optional filter for statuses, " +
            "will return reports that have one of the specified values. " +
            "Valid values: PENDING, IN_PROGRESS, DONE, FAILED, CANCELED, SFTP_DELIVERY_FAILED, EXPIRED")
        @Nullable
        @QueryParam("statuses")
        String statuses,
        @Parameter(
            description = "Optional filter for offset, defaults to 0.") @Nullable @QueryParam("offset") String offset,
        @Parameter(
            description = "Optional filter for limit, defaults to 100.") @Nullable @QueryParam("limit") String limit,
        @Parameter(description = "Time zone to be used when representing dates.") @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRunnerQueryRestException,
        ReportRunnerRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a report runner.")
    ReportRunnerResponse createReportRunner(
        @UserAccessTokenParam String accessToken,
        ReportRunnerCreateRequest request,
        @Parameter(description = "Time zone to be used when representing dates.") @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, ReportRunnerValidationRestException;

    @GET
    @Path("/{reportRunnerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get report runner for the specified id.")
    ReportRunnerResponse getReportRunner(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The Extole unique report runner identifier.") @PathParam("reportRunnerId")
        String reportRunnerId,
        @Parameter(description = "Time zone to be used when representing dates.") @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, ReportRunnerRestException;

    @PUT
    @Path("/{reportRunnerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update report runner for the specified id.")
    ReportRunnerResponse updateReportRunner(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The Extole unique report runner identifier.")
        @PathParam(REPORT_RUNNER_ID_PATH_PARAM_NAME)
        String reportRunnerId,
        ReportRunnerUpdateRequest request,
        @Parameter(description = "Time zone to be used when representing dates.") @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, ReportRunnerRestException, ReportRunnerValidationRestException;

    @DELETE
    @Path("/{reportRunnerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete report runner for the specified id.")
    ReportRunnerResponse deleteReportRunner(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The Extole unique report runner identifier.")
        @PathParam("reportRunnerId")
        String reportRunnerId,
        @Parameter(description = "Time zone to be used when representing dates.") @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, ReportRunnerRestException, ReportRunnerValidationRestException;

    @POST
    @Path("/{reportRunnerId}/run")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Execute report runner for the specified id. " +
        "Will return existing report if there is any currently in progress or generate a new one otherwise.")
    BaseReportRunnerReportResponse run(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The Extole unique report runner identifier.") @PathParam("reportRunnerId")
        String reportRunnerId,
        Optional<ReportRunnerSlotsRequest> request,
        @Parameter(description = "Time zone to be used when representing dates.") @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, ReportRunnerRestException, ReportValidationRestException,
        ReportRunnerValidationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/template")
    @Operation(summary = "Gets a list of templates")
    List<? extends ReportRunnerResponse> getTemplateReportRunners(@UserAccessTokenParam String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{reportRunnerId}/duplicate")
    @Operation(summary = "Duplicates a report runner")
    ReportRunnerResponse duplicate(@UserAccessTokenParam String accessToken,
        @PathParam("reportRunnerId") String reportRunnerId,
        @Nullable @QueryParam("allow_duplicate") Boolean allowDuplicate,
        ReportRunnerUpdateRequest duplicateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportRunnerRestException, ReportRunnerValidationRestException;

    @GET
    @Path("/{reportRunnerId}/rolling-reports")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a list of rolling reports")
    List<ReportResponse> getRollingReports(
        @UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report runner identifier.")
        @PathParam("reportRunnerId")
        String reportRunnerId,
        @Parameter(description = "Optional filter for statuses, " +
            "will return reports that have one of the specified values. " +
            "Valid values: PENDING, IN_PROGRESS, DONE, FAILED, CANCELED, SFTP_DELIVERY_FAILED, EXPIRED")
        @Nullable
        @QueryParam("statuses")
        String statuses,
        @Parameter(
            description = "Optional filter for offset, defaults to 0.") @Nullable @QueryParam("offset") String offset,
        @Parameter(
            description = "Optional filter for limit, defaults to 100.") @Nullable @QueryParam("limit") String limit,
        @Parameter(description = "Time zone to be used when representing dates.") @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRunnerQueryRestException;

    @GET
    @Path("/{reportRunnerId}/latest")
    @Produces(MediaType.APPLICATION_JSON)
    ReportResponse getLatestReport(@UserAccessTokenParam(requiredScope = Scope.ANY) String accessToken,
        @PathParam("reportRunnerId") String reportRunnerId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportRunnerRestException;

    @GET
    @Path("/{reportRunnerId}/latest/download{format :(\\.csv|\\.json|\\.jsonl|\\.xlsx|\\.psv|\\.headless_csv" +
        "|\\.headless_psv)?}")
    Response downloadLatestReport(@AccessTokenParam String accessToken,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @Nullable @PathParam("format") String format,
        @PathParam("reportRunnerId") String reportRunnerId,
        @Nullable @QueryParam("limit") String limit,
        @Nullable @QueryParam("offset") String offset,
        @Nullable @QueryParam("filename") String filename)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRestException, ReportRunnerRestException,
        ReportNotFoundRestException, ReportDownloadRestException;

    @GET
    @Path("/{reportRunnerId}/accumulating-reports")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a list of accumulating reports")
    List<ReportResponse> getAccumulatingReports(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The Extole unique report runner identifier.") @PathParam("reportRunnerId")
        String reportRunnerId,
        @Parameter(description = "Optional filter for statuses, " +
            "will return reports that have one of the specified values. " +
            "Valid values: PENDING, IN_PROGRESS, DONE, FAILED, CANCELED, SFTP_DELIVERY_FAILED, EXPIRED")
        @Nullable
        @QueryParam("statuses")
        String statuses,
        @Parameter(
            description = "Optional filter for offset, defaults to 0.") @Nullable @QueryParam("offset") String offset,
        @Parameter(
            description = "Optional filter for limit, defaults to 100.") @Nullable @QueryParam("limit") String limit,
        @Parameter(description = "Time zone to be used when representing dates.") @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRunnerQueryRestException;
}
