package com.extole.common.rest.timezone;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.AbstractValueParamProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractor;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.model.Parameter;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;

@Singleton
final class TimeZoneParamValueParamProvider extends AbstractValueParamProvider {
    private final Provider<ClientCache> clientCache;

    @Inject
    TimeZoneParamValueParamProvider(Provider<MultivaluedParameterExtractorProvider> extractorProvider,
        Provider<ClientCache> clientCache) {
        super(extractorProvider, Parameter.Source.UNKNOWN);
        this.clientCache = clientCache;
    }

    @Override
    protected Function<ContainerRequest, ?> createValueProvider(Parameter parameter) {
        if (!parameter.getRawType().equals(ZoneId.class)) {
            return null;
        }

        MultivaluedParameterExtractor extractor = get(parameter);
        if (extractor == null) {
            return null;
        }
        return new TimeZoneParamValueProvider(extractor, clientCache.get(), !parameter.isEncoded());
    }

    private static final class TimeZoneParamValueProvider implements Function<ContainerRequest, ZoneId> {
        private final List<Function<ContainerRequest, Optional<ZoneId>>> paramValueProviders;
        private final DefaultValueProvider defaultValueProvider;

        TimeZoneParamValueProvider(MultivaluedParameterExtractor<ZoneId> extractor, ClientCache clientCache,
            boolean decode) {
            this.paramValueProviders = Arrays.asList(
                new QueryParamValueProvider(extractor, decode),
                new HeaderParamValueProvider(extractor));
            this.defaultValueProvider = new DefaultValueProvider(clientCache);
        }

        @Override
        public ZoneId apply(ContainerRequest containerRequest) {
            return paramValueProviders.stream()
                .map(provider -> provider.apply(containerRequest))
                .flatMap(zoneId -> zoneId.map(Stream::of).orElseGet(Stream::empty))
                .findFirst()
                .orElse(defaultValueProvider.apply(containerRequest));
        }
    }

    private static final class QueryParamValueProvider implements Function<ContainerRequest, Optional<ZoneId>> {
        private static final String TIME_ZONE_PARAMETER = "time_zone";

        private final MultivaluedParameterExtractor<ZoneId> extractor;
        private final boolean decode;

        QueryParamValueProvider(MultivaluedParameterExtractor<ZoneId> extractor, boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }

        @Override
        public Optional<ZoneId> apply(ContainerRequest containerRequest) {
            MultivaluedMap<String, String> values = new MultivaluedHashMap<>();
            values.putSingle(TimeZoneParameterService.PARAMETER_NAME,
                containerRequest.getUriInfo().getQueryParameters(decode).getFirst(TIME_ZONE_PARAMETER));

            try {
                return Optional.ofNullable(extractor.extract(values));
            } catch (ExtractorException e) {
                throw new TimeZoneParamException(e.getCause(), TIME_ZONE_PARAMETER, extractor.getDefaultValueString());
            }
        }
    }

    private static final class HeaderParamValueProvider implements Function<ContainerRequest, Optional<ZoneId>> {
        private static final String TIME_ZONE_HEADER = "Time-Zone";

        private final MultivaluedParameterExtractor<ZoneId> extractor;

        HeaderParamValueProvider(MultivaluedParameterExtractor<ZoneId> extractor) {
            this.extractor = extractor;
        }

        @Override
        public Optional<ZoneId> apply(ContainerRequest containerRequest) {
            MultivaluedMap<String, String> values = new MultivaluedHashMap<>();
            values.putSingle(TimeZoneParameterService.PARAMETER_NAME,
                containerRequest.getHeaders().getFirst(TIME_ZONE_HEADER));

            try {
                return Optional.ofNullable(extractor.extract(values));
            } catch (ExtractorException e) {
                throw new TimeZoneParamException(e.getCause(), TIME_ZONE_HEADER, extractor.getDefaultValueString());
            }
        }
    }

    private static final class DefaultValueProvider implements Function<ContainerRequest, ZoneId> {
        private final ClientCache clientCache;

        private DefaultValueProvider(ClientCache clientCache) {
            this.clientCache = clientCache;
        }

        @Override
        public ZoneId apply(ContainerRequest containerRequest) {
            Authorization authorization = (Authorization) containerRequest
                .getProperty(RequestContextAttributeName.AUTHORIZATION.getAttributeName());

            if (authorization == null) {
                return null;
            }
            try {
                return clientCache.getById(authorization.getClientId()).getTimeZone();
            } catch (ClientNotFoundException e) {
                throw new InternalServerErrorException(
                    "Failed to get client settings for client: " + authorization.getClientId(), e);
            }
        }
    }
}
