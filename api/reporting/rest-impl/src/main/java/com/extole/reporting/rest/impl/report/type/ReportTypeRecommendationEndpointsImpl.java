package com.extole.reporting.rest.impl.report.type;

import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.reporting.entity.report.ReportType;
import com.extole.reporting.rest.impl.report.type.mappers.ReportTypeResponseMapper;
import com.extole.reporting.rest.report.type.ReportTypeRecommendationEndpoints;
import com.extole.reporting.rest.report.type.ReportTypeResponse;
import com.extole.reporting.service.report.type.ReportTypeRecommendationService;

@Provider
public class ReportTypeRecommendationEndpointsImpl implements ReportTypeRecommendationEndpoints {
    private static final int DEFAULT_LIMIT = 5;

    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportTypeResponseMappersRepository reportTypeResponseMappersRepository;
    private final ReportTypeRecommendationService reportTypeRecommendationService;

    @Autowired
    public ReportTypeRecommendationEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        ReportTypeResponseMappersRepository reportTypeResponseMappersRepository,
        ReportTypeRecommendationService reportTypeRecommendationService) {
        this.authorizationProvider = authorizationProvider;
        this.reportTypeResponseMappersRepository = reportTypeResponseMappersRepository;
        this.reportTypeRecommendationService = reportTypeRecommendationService;
    }

    @Override
    public List<? extends ReportTypeResponse> getRecommendations(String accessToken, Optional<String> limit,
        ZoneId timezone)
        throws UserAuthorizationRestException, QueryLimitsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<ReportType> reportTypes = reportTypeRecommendationService.getRecommendations(
                authorization,
                parseLimit(limit.orElse(null), DEFAULT_LIMIT));
            return toReportTypesResponse(authorization, timezone, reportTypes);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T extends ReportTypeResponse> T toReportTypeResponse(Authorization authorization, ZoneId clientTimezone,
        ReportType reportType) {
        ReportTypeResponseMapper responseMapper =
            reportTypeResponseMappersRepository.getReportTypeResponseMapper(reportType.getType());
        return (T) responseMapper.toReportTypeResponse(authorization, clientTimezone, reportType);
    }

    private List<ReportTypeResponse> toReportTypesResponse(Authorization authorization, ZoneId clientTimezone,
        List<? extends ReportType> reportTypes) {
        List<ReportTypeResponse> responses = Lists.newArrayList();
        for (ReportType reportType : reportTypes) {
            responses.add(toReportTypeResponse(authorization, clientTimezone, reportType));
        }
        return responses;
    }
}
