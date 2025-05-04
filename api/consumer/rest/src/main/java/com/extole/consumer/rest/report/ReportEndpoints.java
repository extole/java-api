package com.extole.consumer.rest.report;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Path(ReportEndpoints.REPORT_URI)
public interface ReportEndpoints {

    String REPORT_URI = "/v4/reports";
    String REPORT_ID_PARAM = "{reportId}";
    String DOWNLOAD_URI = "/" + REPORT_ID_PARAM + "/download";

    @GET
    @Path("/{reportId}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportResponse readReport(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("reportId") String reportId) throws AuthorizationRestException, ReportRestException;

    @GET
    @Path("/{reportId}/info/{format :(csv|json|jsonl|xlsx|psv|headless_csv|headless_psv)}")
    @Produces(MediaType.APPLICATION_JSON)
    FormatReportInfoResponse getReportInfo(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("reportId") String reportId,
        @PathParam("format") String format) throws AuthorizationRestException, ReportRestException;

    @GET
    @Path("/{reportId}/download{format :(\\.csv|\\.json|\\.jsonl|\\.xlsx|\\.psv|\\.headless_csv|\\.headless_psv)?}")
    Response downloadReport(@AccessTokenParam(readCookie = false) String accessToken,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @PathParam("reportId") String reportId,
        @PathParam("format") String format) throws AuthorizationRestException, ReportRestException;

    @GET
    @Path("/latest")
    @Produces(MediaType.APPLICATION_JSON)
    ReportResponse readLatestReport(@AccessTokenParam(readCookie = false) String accessToken,
        @Nullable @QueryParam("tags") String tags,
        @Nullable @QueryParam("exclude_tags") String excludeTags)
        throws AuthorizationRestException, ReportRestException;

    @GET
    @Path("/latest/download{format :(\\.csv|\\.json|\\.jsonl|\\.xlsx|\\.psv|\\.headless_csv|\\.headless_psv)?}")
    Response downloadLatestReport(@AccessTokenParam(readCookie = false) String accessToken,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @Nullable @QueryParam("tags") String tags,
        @Nullable @QueryParam("exclude_tags") String excludeTags,
        @PathParam("format") String format) throws AuthorizationRestException, ReportRestException;
}
