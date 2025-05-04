package com.extole.reporting.rest.report.type;

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

@Path("/v6/report-types/recommendations")
@Tag(name = "/v6/report-types/recommendations", description = "ReportTypeRecommendation")
public interface ReportTypeRecommendationEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a list of report types that are recommended for execution.")
    List<? extends ReportTypeResponse> getRecommendations(@UserAccessTokenParam String accessToken,
        @Parameter(description = "Optional filter for limit, defaults to 5.")
        @QueryParam("limit") Optional<String> limit,
        @Parameter(description = "Time zone to be used when representing dates.")
        @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, QueryLimitsRestException;

}
