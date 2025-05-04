package com.extole.reporting.rest.report;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v4/report-types")
public interface ReportTypeV4Endpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ReportTypeV4Response> listReportTypes(@UserAccessTokenParam String accessToken)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportTypeV4Response readReportType(@UserAccessTokenParam String accessToken, @PathParam("name") String name)
        throws UserAuthorizationRestException, ReportTypeRestException;

}
