package com.extole.reporting.rest.impl.report;

import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;
import static com.extole.common.rest.support.parser.QueryLimitsParser.parseOffset;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.CampaignHandle;
import com.extole.person.service.StepName;
import com.extole.person.service.profile.journey.Container;
import com.extole.reporting.rest.report.DimensionStatsEndpoints;
import com.extole.reporting.rest.report.DimensionStatsGetRequest;
import com.extole.reporting.rest.report.DimensionStatsResponse;
import com.extole.reporting.rest.report.SummaryDimensionsListRequest;
import com.extole.reporting.service.report.DimensionStatsQueryBuilder;
import com.extole.reporting.service.report.DimensionStatsService;
import com.extole.reporting.service.report.DimensionTimePeriod;
import com.extole.reporting.service.step_summary.StepSummaryDimensionsQueryBuilder;

@Provider
public class DimensionStatsEndpointsImpl implements DimensionStatsEndpoints {
    private static final int DEFAULT_LIMIT = 5;

    private final ClientAuthorizationProvider authorizationProvider;
    private final DimensionStatsService dimensionStatsService;

    @Autowired
    public DimensionStatsEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        DimensionStatsService dimensionStatsService) {
        this.authorizationProvider = authorizationProvider;
        this.dimensionStatsService = dimensionStatsService;
    }

    @Override
    public List<DimensionStatsResponse> getDimensionStats(String accessToken, String dimensionName,
        DimensionStatsGetRequest request) throws UserAuthorizationRestException, QueryLimitsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        DimensionStatsQueryBuilder queryBuilder;
        try {
            queryBuilder = dimensionStatsService.getDimensionStats(authorization, dimensionName);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
        request.getCampaignId().map(Id::<CampaignHandle>valueOf).ifPresent(queryBuilder::withCampaignId);
        request.getProgramLabel().ifPresent(queryBuilder::withProgramLabel);
        request.getContainer().map(Container::new).ifPresent(queryBuilder::withContainer);
        request.getTimePeriod().map(period -> DimensionTimePeriod.valueOf(period.name()))
            .ifPresent(queryBuilder::withTimePeriod);
        if (request.getLimit().isPresent()) {
            queryBuilder.withLimit(parseLimit(request.getLimit().get().toString(), DEFAULT_LIMIT));
        }
        if (request.getOffset().isPresent()) {
            queryBuilder.withOffset(parseOffset(request.getOffset().get().toString(), BigDecimal.ZERO.intValue()));
        }

        return queryBuilder.execute().stream()
            .map(value -> new DimensionStatsResponse(value.getValue(), value.getEventCount()))
            .collect(Collectors.toList());
    }

    @Override
    public List<List<String>> getSummaryDimensions(String accessToken, SummaryDimensionsListRequest request)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        StepSummaryDimensionsQueryBuilder queryBuilder;
        try {
            queryBuilder = dimensionStatsService.getDimensions(authorization);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }

        request.getCampaignId().map(Id::<CampaignHandle>valueOf).ifPresent(queryBuilder::withCampaignId);
        request.getProgramLabel().ifPresent(queryBuilder::withProgramLabel);
        request.getContainer().map(Container::new).ifPresent(queryBuilder::withContainer);
        request.getStepName().map(StepName::valueOf).ifPresent(queryBuilder::withStepName);

        return queryBuilder.execute();
    }
}
