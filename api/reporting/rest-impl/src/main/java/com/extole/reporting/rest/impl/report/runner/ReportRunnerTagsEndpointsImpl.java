package com.extole.reporting.rest.impl.report.runner;

import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;
import static com.extole.common.rest.support.parser.QueryLimitsParser.parseOffset;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.reporting.rest.report.runner.ReportRunnerTagsEndpoints;
import com.extole.reporting.rest.report.runner.ReportRunnersTagsListRequest;
import com.extole.reporting.service.report.runner.ReportRunnerService;
import com.extole.reporting.service.report.runner.ReportRunnerTagQueryBuilder;

@Provider
public class ReportRunnerTagsEndpointsImpl implements ReportRunnerTagsEndpoints {

    private static final int DEFAULT_LIMIT = 100;

    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportRunnerService reportRunnerService;

    @Autowired
    public ReportRunnerTagsEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ReportRunnerService reportRunnerService,
        @Context HttpHeaders requestHeaders) {
        this.authorizationProvider = authorizationProvider;
        this.reportRunnerService = reportRunnerService;
    }

    @Override
    public List<String> getReportRunnersTags(String accessToken, ReportRunnersTagsListRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            if (request == null) {
                request = ReportRunnersTagsListRequest.builder().build();
            }
            ReportRunnerTagQueryBuilder queryBuilder = reportRunnerService.getTagsByQuery(authorization);
            request.getHavingAnyTags().filter(StringUtils::isNotEmpty)
                .map(tags -> Arrays.stream(tags.split(",")).distinct().collect(Collectors.toList()))
                .ifPresent(queryBuilder::withHavingAnyTags);
            request.getExcludeHavingAnyTags().filter(StringUtils::isNotEmpty)
                .map(tags -> Arrays.stream(tags.split(",")).distinct().collect(Collectors.toList()))
                .ifPresent(queryBuilder::withExcludeHavingAnyTags);
            queryBuilder
                .withOffset(
                    parseOffset(request.getOffset().orElse(BigDecimal.ZERO.toString()), BigDecimal.ZERO.intValue()))
                .withLimit(parseLimit(request.getLimit().orElse(String.valueOf(DEFAULT_LIMIT)), DEFAULT_LIMIT));
            return queryBuilder.list();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }
}
