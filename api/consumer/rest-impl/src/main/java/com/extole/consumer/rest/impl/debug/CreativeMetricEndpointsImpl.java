package com.extole.consumer.rest.impl.debug;

import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.debug.CreativeMetricEndpoints;
import com.extole.consumer.rest.debug.CreativeMetricRequest;
import com.extole.consumer.rest.debug.CreativeMetricResponse;
import com.extole.consumer.rest.debug.CreativeMetricRestException;
import com.extole.consumer.rest.debug.CreativeMetricType;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.id.Id;
import com.extole.model.entity.client.PublicClient;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;

@Provider
public class CreativeMetricEndpointsImpl implements CreativeMetricEndpoints {
    private static final Pattern ALLOWED_CHARACTER_PATTERN = Pattern.compile("^[0-9a-zA-Z_]+");

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final ExtoleMetricRegistry metricRegistry;
    private final ClientCache clientCache;

    @Inject
    public CreativeMetricEndpointsImpl(HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        ExtoleMetricRegistry metricRegistry,
        ClientCache clientCache) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.metricRegistry = metricRegistry;
        this.clientCache = clientCache;
    }

    @Override
    public CreativeMetricResponse record(String accessToken, CreativeMetricRequest request)
        throws CreativeMetricRestException, AuthorizationRestException {
        validateRequest(request);
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        PublicClient client = getClient(authorization.getClientId());
        String key = String.format("com.extole.creative.%s.%s", client.getShortName(), request.getKey());
        try {
            switch (request.getMetricType()) {
                case COUNTER:
                    metricRegistry.counter(key).increment(request.getValue().longValue());
                    break;
                case HISTOGRAM:
                    metricRegistry.histogram(key).update(request.getValue().longValue());
                    break;
                default:
                    throw RestExceptionBuilder.newBuilder(CreativeMetricRestException.class)
                        .withErrorCode(CreativeMetricRestException.INVALID_METRIC_TYPE)
                        .addParameter("metric_type", request.getMetricType())
                        .addParameter("supported_metric_types", CreativeMetricType.values())
                        .build();
            }
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(CreativeMetricRestException.class)
                .withErrorCode(CreativeMetricRestException.KEY_IS_ALREADY_USED)
                .addParameter("metric_type", request.getMetricType())
                .build();
        }

        CreativeLogBuilderImpl creativeLogBuilder = new CreativeLogBuilderImpl();
        String logId;
        String message = String.format("creative %s recorded with value %s for key %s", request.getMetricType(),
            request.getValue(), key);
        logId = creativeLogBuilder.withMessage(message)
            .withClientId(client.getId())
            .withAccessToken(accessToken)
            .withUserAgent(HttpUserAgentExtractor.getInstance().getUserAgent(servletRequest).orElse(null))
            .save();
        return new CreativeMetricResponse(logId);
    }

    private void validateRequest(CreativeMetricRequest request) throws CreativeMetricRestException {
        if (request.getKey() == null) {
            throw RestExceptionBuilder.newBuilder(CreativeMetricRestException.class)
                .withErrorCode(CreativeMetricRestException.MISSING_REQUIRED_FIELD)
                .addParameter("name", "key")
                .build();
        }
        if (!ALLOWED_CHARACTER_PATTERN.matcher(request.getKey()).matches()) {
            throw RestExceptionBuilder.newBuilder(CreativeMetricRestException.class)
                .withErrorCode(CreativeMetricRestException.INVALID_KEY)
                .addParameter("key", request.getKey())
                .build();
        }

        if (request.getMetricType() == null) {
            throw RestExceptionBuilder.newBuilder(CreativeMetricRestException.class)
                .withErrorCode(CreativeMetricRestException.MISSING_REQUIRED_FIELD)
                .addParameter("name", "metric_type")
                .build();
        }
        if (request.getValue() == null) {
            throw RestExceptionBuilder.newBuilder(CreativeMetricRestException.class)
                .withErrorCode(CreativeMetricRestException.MISSING_REQUIRED_FIELD)
                .addParameter("name", "value")
                .build();
        }
    }

    private PublicClient getClient(Id<ClientHandle> clientId) {
        try {
            return clientCache.getById(clientId);
        } catch (ClientNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }
}
