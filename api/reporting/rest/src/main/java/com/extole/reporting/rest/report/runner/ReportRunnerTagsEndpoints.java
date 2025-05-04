package com.extole.reporting.rest.report.runner;

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v6/report-runners-tags")
@Tag(name = "/v6/report-runners-tags", description = "ReportRunnerTags")
public interface ReportRunnerTagsEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a list of report runners tags")
    List<String> getReportRunnersTags(
        @UserAccessTokenParam String accessToken,
        @Nullable @BeanParam ReportRunnersTagsListRequest reportRunnersTagsListRequest)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRunnerQueryRestException;
}
