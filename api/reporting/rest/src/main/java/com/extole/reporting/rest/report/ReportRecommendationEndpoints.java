package com.extole.reporting.rest.report;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.reporting.rest.report.execution.ReportResponse;

@Path("/v4/reports/recommendations")
@Tag(name = "/v4/reports/recommendations", description = "ReportRecommendation")
public interface ReportRecommendationEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a list of reports that are recommended according to the selected recommendation type.")
    List<ReportResponse> getRecommendations(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "Recommendation type, can be one of: SCHEDULE") @QueryParam("type") ReportRecommendationType type,
        @Parameter(
            description = "Optional filter for limit, defaults to 4.") @QueryParam("limit") Optional<String> limit,
        @Parameter(description = "Time zone to be used when representing dates.") @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, QueryLimitsRestException;

}
