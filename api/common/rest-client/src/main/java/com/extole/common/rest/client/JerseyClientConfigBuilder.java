package com.extole.common.rest.client;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.extole.common.lang.date.ExtoleTimeModule;

public final class JerseyClientConfigBuilder {

    public static final ObjectMapper DEFAULT_OBJECT_MAPPER = Jackson2ObjectMapperBuilder.json()
        .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .modules(new ExtoleTimeModule(), new Jdk8Module()).build();

    private final Map<String, Object> properties = new HashMap<>();
    private Optional<Integer> maxTotal = Optional.empty();
    private Optional<Integer> defaultMaxPerRoute = Optional.empty();
    private Optional<Duration> connectionTtl = Optional.empty();
    private Optional<ObjectMapper> objectMapper = Optional.empty();

    private JerseyClientConfigBuilder() {
        properties.put(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, Boolean.TRUE.toString());
    }

    public static JerseyClientConfigBuilder newConfig() {
        return new JerseyClientConfigBuilder();
    }

    public JerseyClientConfigBuilder withMaxTotal(int maxTotal) {
        this.maxTotal = Optional.of(Integer.valueOf(maxTotal));
        return this;
    }

    public JerseyClientConfigBuilder withDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = Optional.of(Integer.valueOf(defaultMaxPerRoute));
        return this;
    }

    public JerseyClientConfigBuilder withConnectionTtl(Duration connectionTtl) {
        this.connectionTtl = Optional.of(connectionTtl);
        return this;
    }

    public JerseyClientConfigBuilder withConnectionTimeout(int connectionTimeout) {
        properties.put(ClientProperties.CONNECT_TIMEOUT, connectionTimeout);
        return this;
    }

    public JerseyClientConfigBuilder withReadTimeout(int readTimeout) {
        properties.put(ClientProperties.READ_TIMEOUT, readTimeout);
        return this;
    }

    public JerseyClientConfigBuilder withRetryHandler(HttpRequestRetryHandler retryHandler) {
        properties.put(ApacheClientProperties.RETRY_HANDLER, retryHandler);
        return this;
    }

    public JerseyClientConfigBuilder withProperty(String requestEntityProcessing, Object object) {
        properties.put(requestEntityProcessing, object);
        return this;
    }

    public JerseyClientConfigBuilder withObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = Optional.ofNullable(objectMapper);
        return this;
    }

    public Client build() {
        ClientConfig config = new ClientConfig();
        config.property(ApacheClientProperties.CONNECTION_MANAGER, createNewConnectionManager());
        config.connectorProvider(new ApacheConnectorProvider());

        for (Map.Entry<String, Object> property : properties.entrySet()) {
            config.property(property.getKey(), property.getValue());
        }

        return ClientBuilder.newClient(config)
            .register(new JacksonJaxbJsonProvider(objectMapper.orElse(DEFAULT_OBJECT_MAPPER),
                JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));
    }

    private HttpClientConnectionManager createNewConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager;

        if (connectionTtl.isPresent()) {
            connectionManager = new PoolingHttpClientConnectionManager(connectionTtl.get().getSeconds(), SECONDS);
        } else {
            connectionManager = new PoolingHttpClientConnectionManager();
        }

        maxTotal.ifPresent(value -> connectionManager.setMaxTotal(value));
        defaultMaxPerRoute.ifPresent(value -> connectionManager.setDefaultMaxPerRoute(value));
        return connectionManager;
    }

    public static LoggingFeature defaultLogger(Logger logger) {
        return new LoggingFeature(new LoggerFacade(logger));
    }

    private static final class LoggerFacade extends java.util.logging.Logger {

        private static final int TRACE_LEVEL_THRESHOLD = Level.FINEST.intValue();
        private static final int DEBUG_LEVEL_THRESHOLD = Level.FINE.intValue();
        private static final int INFO_LEVEL_THRESHOLD = Level.INFO.intValue();
        private static final int WARN_LEVEL_THRESHOLD = Level.WARNING.intValue();

        private final Logger logger;

        private LoggerFacade(Logger logger) {
            super(logger.getName(), null);
            this.logger = logger;
        }

        @Override
        public void log(Level level, String message) {
            int levelValue = level.intValue();
            if (levelValue <= TRACE_LEVEL_THRESHOLD) {
                logger.trace(message);
            } else if (levelValue <= DEBUG_LEVEL_THRESHOLD) {
                logger.debug(message);
            } else if (levelValue <= INFO_LEVEL_THRESHOLD) {
                logger.info(message);
            } else if (levelValue <= WARN_LEVEL_THRESHOLD) {
                logger.warn(message);
            } else {
                logger.error(message);
            }
        }

        @Override
        public boolean isLoggable(Level level) {
            int levelValue = level.intValue();
            if (levelValue <= TRACE_LEVEL_THRESHOLD) {
                return logger.isTraceEnabled();
            } else if (levelValue <= DEBUG_LEVEL_THRESHOLD) {
                return logger.isDebugEnabled();
            } else if (levelValue <= INFO_LEVEL_THRESHOLD) {
                return logger.isInfoEnabled();
            } else if (levelValue <= WARN_LEVEL_THRESHOLD) {
                return logger.isWarnEnabled();
            } else {
                return logger.isErrorEnabled();
            }
        }
    }
}
