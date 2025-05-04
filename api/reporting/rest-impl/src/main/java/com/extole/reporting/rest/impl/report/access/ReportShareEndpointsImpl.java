package com.extole.reporting.rest.impl.report.access;

import java.time.Instant;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.InvalidExpiresAtException;
import com.extole.authorization.service.client.resource.ResourceAuthorizationBuilder;
import com.extole.authorization.service.client.resource.ResourceAuthorizationService;
import com.extole.authorization.service.client.user.UserAuthorization;
import com.extole.authorization.service.resource.Resource;
import com.extole.authorization.service.resource.ResourceAuthorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.reporting.entity.report.Report;
import com.extole.reporting.rest.report.access.ReportShareEndpoints;
import com.extole.reporting.rest.report.access.ReportShareRestException;
import com.extole.reporting.rest.report.access.ReportSharedTokenCreateRequest;
import com.extole.reporting.rest.report.access.ReportSharedTokenResponse;
import com.extole.reporting.service.ReportNotFoundException;
import com.extole.reporting.service.report.ReportService;

@Provider
public class ReportShareEndpointsImpl implements ReportShareEndpoints {

    private final ClientAuthorizationProvider clientAuthorizationProvider;
    private final ResourceAuthorizationService resourceAuthorizationService;
    private final ReportService reportService;

    @Autowired
    public ReportShareEndpointsImpl(ClientAuthorizationProvider clientAuthorizationProvider,
        ResourceAuthorizationService resourceAuthorizationService,
        ReportService reportService) {
        this.clientAuthorizationProvider = clientAuthorizationProvider;
        this.resourceAuthorizationService = resourceAuthorizationService;
        this.reportService = reportService;
    }

    @Override
    public ReportSharedTokenResponse createToken(String accessToken,
        String inputReportId,
        ReportSharedTokenCreateRequest request)
        throws ReportShareRestException, UserAuthorizationRestException {
        UserAuthorization authorization = clientAuthorizationProvider.getUserAuthorization(accessToken);
        try {
            Id<Report> reportId = Id.valueOf(inputReportId);
            reportService.getReportById(authorization, reportId);
            ResourceAuthorizationBuilder builder = resourceAuthorizationService.create(authorization)
                .addResource(Resource.Type.PUBLIC_REPORT, reportId);
            request.getDurationSeconds()
                .ifPresent(
                    durationSeconds -> builder.withExpiresAt(Instant.now().plusSeconds(durationSeconds.longValue())));
            return mapToResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (InvalidExpiresAtException e) {
            throw RestExceptionBuilder.newBuilder(ReportShareRestException.class)
                .withErrorCode(ReportShareRestException.INVALID_DURATION)
                .addParameter("duration_seconds", request.getDurationSeconds().orElse(null))
                .withCause(e)
                .build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportShareRestException.class)
                .withErrorCode(ReportShareRestException.NO_SUCH_RESOURCE)
                .withCause(e)
                .build();
        }
    }

    private ReportSharedTokenResponse mapToResponse(ResourceAuthorization resourceAuthorization) {
        return ReportSharedTokenResponse.builder()
            .withToken(resourceAuthorization.getAccessToken())
            .withExpiresIn(resourceAuthorization.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond())
            .build();
    }
}
