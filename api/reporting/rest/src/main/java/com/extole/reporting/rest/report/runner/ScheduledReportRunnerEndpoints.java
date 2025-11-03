package com.extole.reporting.rest.report.runner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.reporting.rest.report.execution.ReportResponse;

@Path("/v6/report-runners/scheduled/{reportRunnerId}")
public interface ScheduledReportRunnerEndpoints {

    @POST
    @Path("/generate-missing-reports")
    @Produces(MediaType.APPLICATION_JSON)
    List<ReportResponse> scheduleMissingReports(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report runner identifier.") @PathParam("reportRunnerId") String reportRunnerId,
        @QueryParam("slot") Optional<ZonedDateTime> slotRequest,
        @QueryParam("limit") Optional<Integer> missingReportsToGenerate,
        @Parameter(description = "Time zone to be used when representing dates.") @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, ReportRunnerRestException, ReportRunnerValidationRestException,
        ScheduledReportRunnerRestException, ReportRunnerQueryRestException;

    @DELETE
    @Path("/reports")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete reports belonging to the specified report runner id")
    List<ReportResponse> deleteReports(
        @UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report runner identifier.") @PathParam("reportRunnerId") String reportRunnerId,
        @QueryParam("slot") Optional<ZonedDateTime> slotRequest,
        @Parameter(description = "Time zone to be used when representing dates.") @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, ReportRunnerRestException, ReportRunnerValidationRestException;
}
