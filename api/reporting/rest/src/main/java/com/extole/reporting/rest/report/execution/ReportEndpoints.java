package com.extole.reporting.rest.report.execution;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

// TODO follow a standard endpoint pattern END-21734
@Path(ReportEndpoints.REPORT_URI)
public interface ReportEndpoints {

    String REPORT_URI = "/v4/reports";
    String DOWNLOAD_URI = "/{reportId}/download";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ReportResponse createReport(@UserAccessTokenParam String accessToken, CreateReportRequest request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportValidationRestException, ReportTypeNotFoundRestException;

    @POST
    @Path("/{reportId}/retry")
    @Produces(MediaType.APPLICATION_JSON)
    ReportResponse retryReport(@UserAccessTokenParam String accessToken, @PathParam("reportId") String reportId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportNotFoundRestException, ReportTypeNotFoundRestException;

    @PUT
    @Path("/{reportId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ReportResponse updateReport(@UserAccessTokenParam String accessToken, @PathParam("reportId") String reportId,
        UpdateReportRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportValidationRestException, ReportNotFoundRestException;

    @GET
    @Path("/{reportId}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportResponse readReport(@UserAccessTokenParam(requiredScope = Scope.ANY) String accessToken,
        @PathParam("reportId") String reportId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportNotFoundRestException;

    @GET
    @Path("/{reportId}/download{format :(\\.csv|\\.json|\\.jsonl|\\.xlsx|\\.psv|\\.headless_csv|\\.headless_psv)?}")
    Response downloadReport(@UserAccessTokenParam(requiredScope = Scope.ANY) String accessToken,
        @Nullable @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @PathParam("reportId") String reportId,
        @Nullable @PathParam("format") String format,
        @Nullable @QueryParam("limit") String limit,
        @Nullable @QueryParam("offset") String offset,
        @Nullable @QueryParam("filename") String filename)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRestException,
        ReportNotFoundRestException, ReportDownloadRestException;

    @GET
    @Path("/{reportId}/info/{format :(csv|json|jsonl|xlsx|psv|headless_csv|headless_psv)}")
    @Produces(MediaType.APPLICATION_JSON)
    FormatReportInfoResponse getReportInfoByFormat(@UserAccessTokenParam(requiredScope = Scope.ANY) String accessToken,
        @PathParam("reportId") String reportId,
        @PathParam("format") String format)
        throws UserAuthorizationRestException, ReportRestException, ReportNotFoundRestException;

    @GET
    @Path("/{reportId}/info")
    @Produces(MediaType.APPLICATION_JSON)
    ReportInfoResponse getReportInfo(@UserAccessTokenParam(requiredScope = Scope.ANY) String accessToken,
        @PathParam("reportId") String reportId)
        throws UserAuthorizationRestException, ReportRestException, ReportNotFoundRestException;

    @POST
    @Path("/{reportId}/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    ReportResponse cancelReport(@UserAccessTokenParam String accessToken, @PathParam("reportId") String reportId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportNotFoundRestException, ReportValidationRestException;

    @DELETE
    @Path("/{reportId}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportResponse deleteReport(@UserAccessTokenParam String accessToken, @PathParam("reportId") String reportId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ReportNotFoundRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ReportResponse> listReports(@UserAccessTokenParam String accessToken,
        @Nullable @BeanParam ReportListRequest reportScheduleListRequest)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportListQueryRestException;

    @GET
    @Path("/latest")
    @Produces(MediaType.APPLICATION_JSON)
    ReportResponse getLatestReport(@UserAccessTokenParam(requiredScope = Scope.ANY) String accessToken,
        @Nullable @BeanParam LatestReportRequest latestReportRequest)
        throws UserAuthorizationRestException, ReportRestException;

    @GET
    @Path("/latest/download{format :(\\.csv|\\.json|\\.jsonl|\\.xlsx|\\.psv|\\.headless_csv|\\.headless_psv)?}")
    Response downloadLatestReport(@AccessTokenParam String accessToken,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @Nullable @PathParam("format") String format,
        @Nullable @BeanParam LatestReportDownloadRequest latestReportDownloadRequest)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRestException,
        ReportNotFoundRestException, ReportDownloadRestException;

    @GET
    @Path("/{reportId}/debug")
    @Produces(MediaType.APPLICATION_JSON)
    ReportDebugResponse readReportDebug(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("reportId") String reportId)
        throws UserAuthorizationRestException, ReportNotFoundRestException;
}
