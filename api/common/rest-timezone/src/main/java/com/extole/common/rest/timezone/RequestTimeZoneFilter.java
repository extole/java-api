package com.extole.common.rest.timezone;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.id.Id;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;

@javax.ws.rs.ext.Provider
public class RequestTimeZoneFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestTimeZoneFilter.class);

    private final List<Function<ContainerRequestContext, Optional<ZoneId>>> zoneIdProviders;
    private final DefaultValueProvider defaultZoneIdProvider;

    @Autowired
    public RequestTimeZoneFilter(ClientCache clientCache) {
        this.zoneIdProviders = Arrays.asList(
            new QueryParamValueProvider(),
            new HeaderParamValueProvider());
        this.defaultZoneIdProvider = new DefaultValueProvider(clientCache);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        ZoneId zoneId = zoneIdProviders
            .stream()
            .map(provider -> provider.apply(requestContext))
            .flatMap(zoneIdItem -> zoneIdItem.map(Stream::of).orElseGet(Stream::empty))
            .findFirst()
            .orElse(defaultZoneIdProvider.apply(requestContext));

        requestContext.setProperty(RequestContextAttributeName.REQUEST_TIME_ZONE.getAttributeName(), zoneId);
    }

    private static final class QueryParamValueProvider implements Function<ContainerRequestContext, Optional<ZoneId>> {

        private static final String TIME_ZONE_PARAMETER = "time_zone";

        @Override
        public Optional<ZoneId> apply(ContainerRequestContext containerRequest) {
            String value = containerRequest.getUriInfo().getQueryParameters(Boolean.TRUE).getFirst(TIME_ZONE_PARAMETER);
            if (Strings.isNullOrEmpty(value)) {
                return Optional.empty();
            }
            try {
                return Optional.of(ZoneId.of(value));
            } catch (DateTimeException e) {
                throw new TimeZoneParamException(e.getCause(), TIME_ZONE_PARAMETER, value);
            }
        }
    }

    private static final class HeaderParamValueProvider implements Function<ContainerRequestContext, Optional<ZoneId>> {

        private static final String TIME_ZONE_HEADER = "Time-Zone";

        HeaderParamValueProvider() {
        }

        @Override
        public Optional<ZoneId> apply(ContainerRequestContext containerRequest) {
            String value = containerRequest.getHeaders().getFirst(TIME_ZONE_HEADER);
            if (Strings.isNullOrEmpty(value)) {
                return Optional.empty();
            }
            try {
                return Optional.of(ZoneId.of(value));
            } catch (DateTimeException e) {
                throw new TimeZoneParamException(e.getCause(), TIME_ZONE_HEADER, value);
            }
        }
    }

    private static final class DefaultValueProvider implements Function<ContainerRequestContext, ZoneId> {

        private final ClientCache clientCache;

        private DefaultValueProvider(ClientCache clientCache) {
            this.clientCache = clientCache;
        }

        @Override
        public ZoneId apply(ContainerRequestContext containerRequest) {
            String clientId =
                (String) containerRequest.getProperty(RequestContextAttributeName.CLIENT_ID.getAttributeName());

            if (clientId == null) {
                return ZoneId.of("UTC");
            }

            try {
                return clientCache.getById(Id.valueOf(clientId)).getTimeZone();
            } catch (ClientNotFoundException e) {
                LOG.error("Failed to get client settings, clientId=" + clientId
                    + ". Could not get client time zone, defaulting to UTC.");
                return ZoneId.of("UTC");
            }
        }
    }

}
