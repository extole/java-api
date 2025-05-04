package com.extole.reporting.rest.impl.report.runner;

import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;
import static com.extole.common.rest.support.parser.QueryLimitsParser.parseOffset;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.reporting.entity.report.runner.ReportRunner;
import com.extole.reporting.entity.report.runner.ReportRunnerOrder;
import com.extole.reporting.entity.report.runner.ReportRunnerType;
import com.extole.reporting.rest.impl.report.runner.mappers.ReportRunnerViewResponseMapper;
import com.extole.reporting.rest.report.runner.ReportRunnerQueryRestException;
import com.extole.reporting.rest.report.runner.ReportRunnerRestException;
import com.extole.reporting.rest.report.runner.ReportRunnerViewEndpoints;
import com.extole.reporting.rest.report.runner.ReportRunnerViewResponse;
import com.extole.reporting.rest.report.runner.ReportRunnersListRequest;
import com.extole.reporting.service.report.runner.AggregationStatus;
import com.extole.reporting.service.report.runner.PauseStatus;
import com.extole.reporting.service.report.runner.ReportRunnerNotFoundException;
import com.extole.reporting.service.report.runner.ReportRunnerQueryBuilder;
import com.extole.reporting.service.report.runner.ReportRunnerService;
import com.extole.reporting.service.report.runner.ReportRunnerWrongTypeException;

@Provider
public class ReportRunnerViewEndpointsImpl implements ReportRunnerViewEndpoints {
    private static final int DEFAULT_LIMIT = 100;

    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportRunnerService reportRunnerService;
    private final ReportRunnerViewResponseMappersRepository reportRunnerResponseMappersRepository;

    @Autowired
    public ReportRunnerViewEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ReportRunnerService reportRunnerService,
        ReportRunnerViewResponseMappersRepository reportRunnerResponseMappersRepository,
        @Context HttpHeaders requestHeaders) {
        this.authorizationProvider = authorizationProvider;
        this.reportRunnerService = reportRunnerService;
        this.reportRunnerResponseMappersRepository = reportRunnerResponseMappersRepository;
    }

    @Override
    public ReportRunnerViewResponse getReportRunner(String accessToken, String reportRunnerId,
        ZoneId timezone) throws UserAuthorizationRestException, ReportRunnerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ReportRunner reportRunner = reportRunnerService.getById(authorization, Id.valueOf(reportRunnerId));
            return toReportRunnerResponse(authorization, reportRunner, timezone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", e.getReportRunnerId().getValue())
                .withCause(e).build();
        } catch (ReportRunnerWrongTypeException | ClientNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e)
                .build();
        }
    }

    @Override
    public List<? extends ReportRunnerViewResponse> getReportRunners(String accessToken,
        ReportRunnersListRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRunnerQueryRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            if (request == null) {
                request = ReportRunnersListRequest.builder().build();
            }
            ReportRunnerQueryBuilder queryBuilder = reportRunnerService.getByQuery(authorization);
            if (request.getType().filter(StringUtils::isNotEmpty).isPresent()) {
                queryBuilder.withReportRunnerType(parseReportRunnerType(request.getType().get()));
            }
            request.getDisplayName().filter(StringUtils::isNotEmpty).ifPresent(queryBuilder::withDisplayName);
            request.getReportTypeName().filter(StringUtils::isNotEmpty).ifPresent(queryBuilder::withReportTypeName);
            request.getHavingAnyTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(queryBuilder::withHavingAnyTags);
            request.getHavingAllTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(queryBuilder::withHavingAllTags);
            request.getExcludeHavingAnyTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(queryBuilder::withExcludeHavingAnyTags);
            request.getExcludeHavingAllTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(queryBuilder::withExcludeHavingAllTags);
            request.getSearchQuery().filter(StringUtils::isNotEmpty).ifPresent(queryBuilder::withSearchQuery);
            if (request.getPauseStatus().filter(StringUtils::isNotEmpty).isPresent()) {
                queryBuilder.withPauseStatuses(parsePauseStatuses(request.getPauseStatus().get()));
            }
            if (request.getAggregationStatus().filter(StringUtils::isNotEmpty).isPresent()) {
                queryBuilder.withAggregationStatuses(parseAggregationStatuses(request.getAggregationStatus().get()));
            }
            if (!request.getUserIds().isEmpty()) {
                queryBuilder.withUserIds(request.getUserIds());
            }

            queryBuilder
                .withOffset(
                    parseOffset(request.getOffset().orElse(BigDecimal.ZERO.toString()), BigDecimal.ZERO.intValue()))
                .withLimit(parseLimit(request.getLimit().orElse(String.valueOf(DEFAULT_LIMIT)), DEFAULT_LIMIT))
                .withOrder(request.getOrder().filter(StringUtils::isNotEmpty).map(ReportRunnerOrder.Direction::valueOf)
                    .orElse(ReportRunnerOrder.Direction.DESCENDING))
                .withOrderBy(request.getOrderBy().filter(StringUtils::isNotEmpty).map(ReportRunnerOrder.Field::valueOf)
                    .orElse(ReportRunnerOrder.Field.CREATED_DATE));
            return toReportRunnersResponse(authorization, queryBuilder.list(), request.getTimezone().orElse(null));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportRunnerWrongTypeException | ReportRunnerNotFoundException | ClientNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e)
                .build();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T extends ReportRunnerViewResponse> T toReportRunnerResponse(Authorization authorization,
        ReportRunner reportRunner, ZoneId timezone)
        throws AuthorizationException, ReportRunnerWrongTypeException, ReportRunnerNotFoundException,
        ClientNotFoundException {
        ReportRunnerViewResponseMapper responseMapper =
            reportRunnerResponseMappersRepository.getReportRunnerResponseMapper(reportRunner.getType());
        return (T) responseMapper.toReportRunner(authorization, reportRunner, timezone);
    }

    private ReportRunnerType parseReportRunnerType(String reportRunnerType) throws ReportRunnerQueryRestException {
        try {
            return ReportRunnerType.valueOf(reportRunnerType);
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerQueryRestException.class)
                .withErrorCode(ReportRunnerQueryRestException.REPORT_RUNNER_INVALID_TYPE)
                .addParameter("type", reportRunnerType)
                .withCause(e).build();
        }
    }

    private Set<PauseStatus> parsePauseStatuses(String pauseStatuses) throws ReportRunnerQueryRestException {
        Set<PauseStatus> result = Sets.newHashSet();
        for (String pauseStatus : Strings.nullToEmpty(pauseStatuses).split(",")) {
            if (Strings.isNullOrEmpty(pauseStatus)) {
                continue;
            }
            try {
                result.add(PauseStatus.valueOf(pauseStatus.trim()));
            } catch (IllegalArgumentException e) {
                throw RestExceptionBuilder.newBuilder(ReportRunnerQueryRestException.class)
                    .withErrorCode(ReportRunnerQueryRestException.REPORT_RUNNER_INVALID_PAUSE_STATUS)
                    .addParameter("status", pauseStatus)
                    .withCause(e).build();
            }
        }
        return result;
    }

    private Set<AggregationStatus> parseAggregationStatuses(String aggregationStatuses)
        throws ReportRunnerQueryRestException {
        Set<AggregationStatus> result = Sets.newHashSet();
        for (String aggregationStatus : Strings.nullToEmpty(aggregationStatuses).split(",")) {
            if (Strings.isNullOrEmpty(aggregationStatus)) {
                continue;
            }
            try {
                result.add(AggregationStatus.valueOf(aggregationStatus.trim()));
            } catch (IllegalArgumentException e) {
                throw RestExceptionBuilder.newBuilder(ReportRunnerQueryRestException.class)
                    .withErrorCode(ReportRunnerQueryRestException.REPORT_RUNNER_INVALID_AGGREGATION_STATUS)
                    .addParameter("status", aggregationStatus)
                    .withCause(e).build();
            }
        }
        return result;
    }

    private List<ReportRunnerViewResponse> toReportRunnersResponse(Authorization authorization,
        List<ReportRunner> reportRunners, ZoneId timezone)
        throws AuthorizationException, ReportRunnerWrongTypeException, ReportRunnerNotFoundException,
        ClientNotFoundException {
        List<ReportRunnerViewResponse> responses = Lists.newArrayList();
        for (ReportRunner reportRunner : reportRunners) {
            responses.add(toReportRunnerResponse(authorization, reportRunner, timezone));
        }

        return responses;
    }

    private List<String> parseTagsToList(String havingAllTags) {
        return Arrays.stream(havingAllTags.split(",")).collect(Collectors.toList());
    }
}
