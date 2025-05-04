package com.extole.reporting.rest.report.schedule;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.reporting.rest.report.execution.ReportResponse;
import com.extole.reporting.rest.report.execution.ReportValidationRestException;

@Path(ReportScheduleEndpoints.REPORT_SCHEDULES_URI)
public interface ReportScheduleEndpoints {

    String REPORT_SCHEDULES_URI = "/v2/report-schedules";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ReportScheduleResponse> listReportSchedules(@UserAccessTokenParam String accessToken,
        @Nullable @BeanParam ReportScheduleListRequest reportScheduleListRequest)
        throws UserAuthorizationRestException, QueryLimitsRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ReportScheduleResponse createReportSchedule(@UserAccessTokenParam String accessToken,
        CreateReportScheduleRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException, ReportScheduleValidationRestException;

    @GET
    @Path("/{reportScheduleId}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportScheduleResponse readReportSchedule(@UserAccessTokenParam String accessToken,
        @PathParam("reportScheduleId") String reportScheduleId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException;

    @PUT
    @Path("/{reportScheduleId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ReportScheduleResponse updateReportSchedule(@UserAccessTokenParam String accessToken,
        @PathParam("reportScheduleId") String reportScheduleId, UpdateReportScheduleRequest request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException, ReportScheduleValidationRestException;

    @DELETE
    @Path("/{reportScheduleId}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportScheduleResponse deleteReportSchedule(@UserAccessTokenParam String accessToken,
        @PathParam("reportScheduleId") String reportScheduleId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException, ReportScheduleValidationRestException;

    @POST
    @Path("/{reportScheduleId}/generate-missing-reports")
    @Produces(MediaType.APPLICATION_JSON)
    List<ReportResponse> scheduleMissingReports(@UserAccessTokenParam String accessToken,
        @PathParam("reportScheduleId") String reportScheduleId,
        @QueryParam("limit") Integer missingReportsToGenerate,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException, ReportScheduleValidationRestException;

    @POST
    @Path("/{reportScheduleId}/run")
    @Produces(MediaType.APPLICATION_JSON)
    ReportResponse run(@UserAccessTokenParam String accessToken,
        @PathParam("reportScheduleId") String reportScheduleId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException, ReportScheduleValidationRestException,
        ReportValidationRestException;
}
