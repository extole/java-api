package com.extole.consumer.rest.impl.report;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.report.cache.ConsumerReportCache;
import com.extole.consumer.rest.impl.request.ConsumerContextAttributeName;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.report.FormatReportInfoResponse;
import com.extole.consumer.rest.report.ReportEndpoints;
import com.extole.consumer.rest.report.ReportFormat;
import com.extole.consumer.rest.report.ReportResponse;
import com.extole.consumer.rest.report.ReportRestException;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;

@Provider
public class ReportEndpointsImpl implements ReportEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(ReportEndpointsImpl.class);
    private static final String REPORT_CONTENT_DISPOSITION_FORMATTER = "attachment; filename = report-%s-%s.%s";

    private final ConsumerRequestContextService consumerRequestContextService;
    private final ConsumerReportCache reportingCache;
    private final HttpServletRequest servletRequest;

    public ReportEndpointsImpl(ConsumerRequestContextService consumerRequestContextService,
        ConsumerReportCache reportingCache,
        @Context HttpServletRequest servletRequest) {
        this.consumerRequestContextService = consumerRequestContextService;
        this.servletRequest = servletRequest;
        this.reportingCache = reportingCache;
    }

    @Override
    public ReportResponse readReport(String accessToken, String reportId)
        throws AuthorizationRestException, ReportRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        try {
            ReportResponse response = reportingCache.getReportById(authorization, Id.valueOf(reportId));
            return mapToConsumerContentUri(response);
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_NOT_FOUND)
                .addParameter("query", "report_id=" + reportId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public FormatReportInfoResponse getReportInfo(String accessToken, String reportId, String format)
        throws AuthorizationRestException, ReportRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        try {
            ReportFormat reportFormat = getFormat(format, reportId);
            return reportingCache.getReportInfoById(authorization, Id.valueOf(reportId), reportFormat);
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_NOT_FOUND)
                .addParameter("query", "report_id=" + reportId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public Response downloadReport(String accessToken, String contentType, String reportId, String format)
        throws AuthorizationRestException, ReportRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        try {

            Id<?> reportIdValue = Id.valueOf(reportId);
            ReportResponse reportResponse = reportingCache.getReportById(authorization, reportIdValue);
            ReportFormat reportFormat = getFormat(format, contentType, reportResponse);
            FormatReportInfoResponse downloadInfo =
                reportingCache.getReportInfoById(authorization, reportIdValue, reportFormat);
            StreamingOutput streamer =
                outputStream -> {
                    try {
                        reportingCache.downloadReportContentById(authorization, reportIdValue, reportFormat,
                            outputStream);
                    } catch (ReportNotFoundException | ReportContentLengthException
                        | ReportContentDownloadException e) {
                        throw new ReportRuntimeException(e);
                    }
                };

            Response.ResponseBuilder responseBuilder = Response.ok(streamer, reportFormat.getMimeType());
            responseBuilder.header(HttpHeaders.CONTENT_LENGTH, Long.valueOf(downloadInfo.getContentLength()));
            responseBuilder.header(HttpHeaders.CONTENT_ENCODING, "UTF-8");
            responseBuilder.header(HttpHeaders.CONTENT_DISPOSITION,
                String.format(REPORT_CONTENT_DISPOSITION_FORMATTER, reportResponse.getName(), reportId,
                    reportFormat.getExtension().toLowerCase()));
            return responseBuilder.build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_NOT_FOUND)
                .addParameter("query", "report_id=" + reportId)
                .withCause(e)
                .build();
        } catch (ReportRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AuthorizationException) {
                throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                    .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                    .withCause(e)
                    .build();
            } else if (cause instanceof ReportNotFoundException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_NOT_FOUND)
                    .addParameter("query", "report_id=" + reportId)
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportContentLengthException) {
                LOG.error("Failed to download content for report: {}", reportId, cause);
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_LENGTH_EXCEEDED)
                    .addParameter("report_id", reportId)
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportContentDownloadException) {
                LOG.error("Failed to download content for report: {}", reportId, cause);
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_DOWNLOADED)
                    .addParameter("report_id", reportId)
                    .withCause(cause)
                    .build();
            } else {
                LOG.error("Failed to download content for report: {}", reportId, cause);
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(cause)
                    .build();
            }
        }
    }

    @Override
    public ReportResponse readLatestReport(String accessToken, String tags, String excludeTags)
        throws AuthorizationRestException, ReportRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        try {
            ReportResponse response =
                reportingCache.getReportByTags(authorization, parseTags(tags), parseTags(excludeTags));
            return mapToConsumerContentUri(response);
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_NOT_FOUND)
                .addParameter("query", "tags=" + tags)
                .withCause(e)
                .build();
        }
    }

    @Override
    public Response downloadLatestReport(String accessToken, String contentType, String tags, String excludeTags,
        String format) throws AuthorizationRestException, ReportRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        try {
            ReportResponse reportResponse =
                reportingCache.getReportByTags(authorization, parseTags(tags), parseTags(excludeTags));
            ReportFormat reportFormat = getFormat(format, contentType, reportResponse);
            Id<?> reportId = Id.valueOf(reportResponse.getReportId());
            FormatReportInfoResponse downloadInfo =
                reportingCache.getReportInfoById(authorization, reportId, reportFormat);
            try {
                StreamingOutput streamer =
                    outputStream -> {
                        try {
                            reportingCache.downloadReportContentById(authorization, reportId, reportFormat,
                                outputStream);
                        } catch (ReportNotFoundException | ReportContentLengthException
                            | ReportContentDownloadException e) {
                            throw new ReportRuntimeException(e);
                        }
                    };

                Response.ResponseBuilder responseBuilder = Response.ok(streamer, reportFormat.getMimeType());
                responseBuilder.header(HttpHeaders.CONTENT_LENGTH, Long.valueOf(downloadInfo.getContentLength()));
                responseBuilder.header(HttpHeaders.CONTENT_ENCODING, "UTF-8");
                responseBuilder.header(HttpHeaders.CONTENT_DISPOSITION,
                    String.format(REPORT_CONTENT_DISPOSITION_FORMATTER, reportResponse.getName(), reportId,
                        reportFormat.getExtension().toLowerCase()));
                return responseBuilder.build();
            } catch (ReportRuntimeException e) {
                Throwable cause = e.getCause();
                if (cause instanceof AuthorizationException) {
                    throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                        .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                        .withCause(e)
                        .build();
                } else if (cause instanceof ReportNotFoundException) {
                    throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                        .withErrorCode(ReportRestException.REPORT_NOT_FOUND)
                        .addParameter("query", "report_id=" + reportId)
                        .withCause(cause)
                        .build();
                } else if (cause instanceof ReportContentLengthException) {
                    LOG.error("Failed to download content for report: {}", reportId, cause);
                    throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                        .withErrorCode(ReportRestException.REPORT_CONTENT_LENGTH_EXCEEDED)
                        .addParameter("report_id", reportId)
                        .withCause(cause)
                        .build();
                } else if (cause instanceof ReportContentDownloadException) {
                    LOG.error("Failed to download content for report: {}", reportId, cause);
                    throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                        .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_DOWNLOADED)
                        .addParameter("report_id", reportId)
                        .withCause(cause)
                        .build();
                } else {
                    LOG.error("Failed to download content for report with tags: {}", tags, cause);
                    throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                        .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                        .withCause(cause)
                        .build();
                }
            }
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_NOT_FOUND)
                .addParameter("query", "tags=" + tags)
                .withCause(e)
                .build();
        }
    }

    private List<String> parseTags(String tags) {
        if (!Strings.isNullOrEmpty(tags)) {
            return Arrays.stream(tags.split(",")).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private ReportFormat getFormat(String format, String contentType, ReportResponse report)
        throws ReportRestException {
        if (!Strings.isNullOrEmpty(format)) {
            return getFormat(format.split("\\.")[1], report.getReportId());
        } else if (!Strings.isNullOrEmpty(contentType)) {
            try {
                return ReportFormat.valueOfMimeType(contentType);
            } catch (IllegalArgumentException e) {
                LOG.error("Failed to retrieve content type : {}", report.getReportId(), e);
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_TYPE_NOT_SUPPORTED)
                    .addParameter("report_id", report.getReportId())
                    .addParameter("content_type", contentType)
                    .withCause(e)
                    .build();
            }
        } else {
            return report.getFormats().iterator().next();
        }
    }

    private ReportFormat getFormat(String format, String reportId) throws ReportRestException {
        try {
            return ReportFormat.valueOf(format.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_FORMAT_NOT_SUPPORTED)
                .addParameter("report_id", reportId)
                .addParameter("format", format)
                .withCause(e)
                .build();
        }
    }

    private ReportResponse mapToConsumerContentUri(ReportResponse response) {
        PublicProgram program =
            (PublicProgram) servletRequest.getAttribute(ConsumerContextAttributeName.PROGRAM.getAttributeName());
        String contentUri = program.getScheme() + "://" + program.getProgramDomain().toString() + "/api" + REPORT_URI
            + DOWNLOAD_URI.replace(REPORT_ID_PARAM, response.getReportId());
        return new ReportResponse(response.getReportId(),
            response.getName(),
            response.getReportType(),
            response.getDisplayName(),
            response.getFormats(),
            response.getTags(),
            response.getCompletedDate(),
            contentUri);
    }
}
