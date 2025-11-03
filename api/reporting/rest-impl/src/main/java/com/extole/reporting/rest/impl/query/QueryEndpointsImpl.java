package com.extole.reporting.rest.impl.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.report.type.ReportInvalidParametersException;
import com.extole.model.service.report.ReportMissingParametersException;
import com.extole.model.service.report.type.ReportTypeNotFoundException;
import com.extole.reporting.rest.query.QueryEndpoints;
import com.extole.reporting.rest.query.QueryRestException;
import com.extole.reporting.service.query.QueryExecutionException;
import com.extole.reporting.service.query.QueryService;

@Deprecated // TODO claenup QueryEndpoints ENG-10088
@Provider
public class QueryEndpointsImpl implements QueryEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(QueryEndpointsImpl.class);

    private final UriInfo uriInfo;
    private final ClientAuthorizationProvider authorizationProvider;
    private final QueryService queryService;

    @Inject
    public QueryEndpointsImpl(
        @Context UriInfo uriInfo,
        ClientAuthorizationProvider authorizationProvider, QueryService queryService) {
        this.uriInfo = uriInfo;
        this.authorizationProvider = authorizationProvider;
        this.queryService = queryService;
    }

    @Override
    public List<Map<String, Object>> query(String accessToken, String name)
        throws UserAuthorizationRestException, QueryRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        String cleanedName = name.replace(".csv", "");

        LOG.info("Executing query {} for client {} and token {}", cleanedName, authorization.getClientId(),
            accessToken);

        Map<String, String> parameters = uriInfo.getQueryParameters().entrySet().stream()
            .filter(entry -> !entry.getValue().isEmpty())
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> Iterables.getFirst(entry.getValue(), null)));

        try {
            return queryService.query(authorization, cleanedName, parameters);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (ReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(QueryRestException.class)
                .withErrorCode(QueryRestException.QUERY_UNKNOWN)
                .addParameter("name", cleanedName)
                .withCause(e).build();
        } catch (ReportMissingParametersException e) {
            throw RestExceptionBuilder.newBuilder(QueryRestException.class)
                .withErrorCode(QueryRestException.QUERY_MISSING_PARAMETERS)
                .addParameter("parameters", e.getMissingParameters())
                .withCause(e).build();
        } catch (ReportInvalidParametersException e) {
            throw RestExceptionBuilder.newBuilder(QueryRestException.class)
                .withErrorCode(QueryRestException.QUERY_INVALID_PARAMETERS)
                .addParameter("parameters", e.getParameterNames())
                .withCause(e).build();
        } catch (QueryExecutionException e) {
            throw RestExceptionBuilder.newBuilder(QueryRestException.class)
                .withErrorCode(QueryRestException.QUERY_EXECUTION_ERROR)
                .withCause(e).build();
        }
    }

}
