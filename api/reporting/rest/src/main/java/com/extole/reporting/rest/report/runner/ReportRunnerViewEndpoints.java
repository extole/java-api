package com.extole.reporting.rest.report.runner;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/report-runners")
@Tag(name = "/v6/report-runners/views/detailed", description = "ReportRunnerView")
public interface ReportRunnerViewEndpoints {

    String REPORT_RUNNER_ID_PATH_PARAM_NAME = "reportRunnerId";

    @GET
    @Path("/views/detailed")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a filtered list of report runner views")
    List<? extends ReportRunnerViewResponse> getReportRunners(
        @UserAccessTokenParam String accessToken,
        @Nullable @BeanParam ReportRunnersListRequest reportRunnersListRequest)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRunnerQueryRestException;

    @GET
    @Path("/{" + REPORT_RUNNER_ID_PATH_PARAM_NAME + "}/views/detailed")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get report runner view for the specified id.")
    ReportRunnerViewResponse getReportRunner(
        @UserAccessTokenParam String accessToken,
        @Parameter(description = "The Extole unique report runner identifier.")
        @PathParam(REPORT_RUNNER_ID_PATH_PARAM_NAME) String reportRunnerId,
        @Parameter(description = "Time zone to be used when representing dates.")
        @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, ReportRunnerRestException;
}
