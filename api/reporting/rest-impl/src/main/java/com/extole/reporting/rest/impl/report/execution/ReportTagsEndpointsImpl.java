package com.extole.reporting.rest.impl.report.execution;

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
import com.extole.model.service.report.runner.ReportTagQueryBuilder;
import com.extole.reporting.rest.report.execution.ReportTagsEndpoints;
import com.extole.reporting.rest.report.execution.ReportsTagsListRequest;
import com.extole.reporting.service.report.ReportService;

@Provider
public class ReportTagsEndpointsImpl implements ReportTagsEndpoints {

    private static final int DEFAULT_LIMIT = 100;

    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportService reportService;

    @Autowired
    public ReportTagsEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ReportService reportService,
        @Context HttpHeaders requestHeaders) {
        this.authorizationProvider = authorizationProvider;
        this.reportService = reportService;
    }

    @Override
    public List<String> getReportsTags(String accessToken, ReportsTagsListRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            if (request == null) {
                request = ReportsTagsListRequest.builder().build();
            }
            ReportTagQueryBuilder queryBuilder = reportService.getTagsByQuery(authorization);
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
