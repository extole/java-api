package com.extole.reporting.rest.report.sql;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.reporting.rest.report.ReportTypeRestException;

@Path("/v4/report-types/sql")
public interface SqlReportTypeV4Endpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<SqlReportTypeV4Response> listSqlReportTypes(@UserAccessTokenParam String accessToken,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException;

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    SqlReportTypeV4Response readSqlReportType(@UserAccessTokenParam String accessToken, @PathParam("name") String name,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, ReportTypeRestException;

    /**
     * This endpoint expects Custom as special category that is configured on per client basis unlike all other
     * categories which are global.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    SqlReportTypeV4Response createSqlReportType(@UserAccessTokenParam String accessToken,
        SqlCreateReportTypeV4Request request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, SqlReportTypeValidationRestException;

    /**
     * This endpoint expects Custom as special category that is configured on per client basis unlike all other
     * categories which are global.
     */
    @PUT
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    SqlReportTypeV4Response updateSqlReportType(@UserAccessTokenParam String accessToken,
        @PathParam("name") String name,
        SqlUpdateReportTypeV4Request request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportTypeRestException, SqlReportTypeValidationRestException;

    @DELETE
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    SqlReportTypeV4Response deleteSqlReportType(@UserAccessTokenParam String accessToken,
        @PathParam("name") String name, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportTypeRestException;

}
