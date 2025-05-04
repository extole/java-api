package com.extole.reporting.rest.report.access;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.reporting.rest.report.execution.ReportEndpoints;

@Path(ReportEndpoints.REPORT_URI)
public interface ReportShareEndpoints {
    String URI = "/v4/reports";

    @POST
    @Path("/{reportId}/sharedToken")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ReportSharedTokenResponse createToken(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_ADMIN) String accessToken,
        @PathParam("reportId") String reportId,
        ReportSharedTokenCreateRequest request)
        throws ReportShareRestException, UserAuthorizationRestException;
}
