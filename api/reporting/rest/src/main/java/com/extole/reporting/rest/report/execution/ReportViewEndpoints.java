package com.extole.reporting.rest.report.execution;

import java.time.ZoneId;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path(ReportViewEndpoints.REPORT_URI)
public interface ReportViewEndpoints {

    String REPORT_URI = "/v4/reports";

    @GET
    @Path("/{reportId}/views/public")
    @Produces(MediaType.APPLICATION_JSON)
    PublicReportResponse readPublicReport(@UserAccessTokenParam(requiredScope = Scope.ANY) String accessToken,
        @PathParam("reportId") String reportId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportNotFoundRestException;

}
