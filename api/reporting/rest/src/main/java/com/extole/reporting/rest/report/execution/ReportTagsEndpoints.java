package com.extole.reporting.rest.report.execution;

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path(ReportTagsEndpoints.REPORT_TAG_URI)
public interface ReportTagsEndpoints {

    String REPORT_TAG_URI = "/v4/reports-tags";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a list of report tags")
    List<String> getReportsTags(@UserAccessTokenParam String accessToken,
        @Nullable @BeanParam ReportsTagsListRequest reportsTagsListRequest)
        throws UserAuthorizationRestException, QueryLimitsRestException;

}
