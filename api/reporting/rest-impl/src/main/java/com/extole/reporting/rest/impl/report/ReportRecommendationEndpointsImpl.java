package com.extole.reporting.rest.impl.report;

import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.reporting.rest.report.ReportRecommendationEndpoints;
import com.extole.reporting.rest.report.ReportRecommendationType;
import com.extole.reporting.rest.report.execution.ReportResponse;
import com.extole.reporting.service.report.ReportRecommendationService;

@Provider
public class ReportRecommendationEndpointsImpl implements ReportRecommendationEndpoints {
    private static final int DEFAULT_LIMIT = 4;

    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportResponseMapper reportResponseMapper;
    private final ReportRecommendationService reportRecommendationService;
    private final HttpHeaders requestHeaders;

    @Autowired
    public ReportRecommendationEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        ReportResponseMapper reportResponseMapper,
        ReportRecommendationService reportRecommendationService,
        @Context HttpHeaders requestHeaders) {
        this.authorizationProvider = authorizationProvider;
        this.reportResponseMapper = reportResponseMapper;
        this.reportRecommendationService = reportRecommendationService;
        this.requestHeaders = requestHeaders;
    }

    @Override
    public List<ReportResponse> getRecommendations(String accessToken, ReportRecommendationType type,
        Optional<String> limit, ZoneId timezone) throws UserAuthorizationRestException, QueryLimitsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return reportRecommendationService.getRecommendations(
                authorization,
                com.extole.reporting.service.report.ReportRecommendationType.valueOf(type.name()),
                parseLimit(limit.orElse(null), DEFAULT_LIMIT))
                .stream()
                .map(report -> reportResponseMapper.toReportResponse(authorization, report, requestHeaders,
                    timezone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }
}
