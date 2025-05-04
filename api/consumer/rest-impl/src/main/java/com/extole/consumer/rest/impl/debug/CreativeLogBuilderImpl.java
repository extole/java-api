package com.extole.consumer.rest.impl.debug;

import static org.slf4j.LoggerFactory.getLogger;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import net.logstash.logback.marker.MapEntriesAppendingMarker;
import org.slf4j.Logger;
import org.slf4j.Marker;

import com.extole.authorization.service.ClientHandle;
import com.extole.consumer.rest.debug.CreativeLogLevel;
import com.extole.id.Id;

public class CreativeLogBuilderImpl {
    private static final Logger CREATIVE_LOGGER = getLogger("CREATIVE_LOGGER");
    private static final Logger MOBILE_APP_LOGGER = getLogger("MOBILE_APP_LOGGER");

    private static final Set<String> MOBILE_APP_LOGS_IDENTIFIERS = Set.of("tags=mobile-sdk", "[mobile-sdk, android]");

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String NO_CLIENT = "no_client_id";
    private static final String NO_USER_AGENT = "no_user_agent";
    private static final String CUSTOM_LOG_ID_PREFIX = "LOG_";
    private static final int NUMBER_OF_BITS_RANDOM_GENERATOR = 130;
    private static final int RADIX = 32;

    private String message;
    private CreativeLogLevel level;
    private String accessToken;
    private String userAgent;
    private Id<ClientHandle> clientId;

    public CreativeLogBuilderImpl() {
        this.level = CreativeLogLevel.INFO;
    }

    public CreativeLogBuilderImpl withMessage(String message) {
        this.message = message;
        return this;
    }

    public CreativeLogBuilderImpl withLevel(CreativeLogLevel level) {
        this.level = level;
        return this;
    }

    public CreativeLogBuilderImpl withClientId(Id<ClientHandle> clientId) {
        this.clientId = clientId;
        return this;
    }

    public CreativeLogBuilderImpl withAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public CreativeLogBuilderImpl withUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String save() {
        String pollingId = generatePollingId();

        String sanitizedAccessToken = Strings.isNullOrEmpty(accessToken) ? dummyAccessToken(pollingId) : accessToken;
        String sanitizedClientId = clientId == null ? NO_CLIENT : clientId.getValue();
        String sanitizedUserAgent = Strings.isNullOrEmpty(userAgent) ? NO_USER_AGENT : userAgent;

        Map<String, String> logMap = Map.of(
            "polling_id", pollingId,
            "client_id", sanitizedClientId,
            "access_token", sanitizedAccessToken,
            "user_agent", sanitizedUserAgent);
        Marker logstashMarker = new MapEntriesAppendingMarker(logMap);

        Logger logger = pickLogger(message);

        switch (level) {
            case ERROR:
                logger.error(logstashMarker, message);
                break;
            case WARN:
                logger.warn(logstashMarker, message);
                break;
            case INFO:
                logger.info(logstashMarker, message);
                break;
            default:
                logger.debug(logstashMarker, message);
        }

        return pollingId;
    }

    private static Logger pickLogger(String message) {
        return message != null
            && MOBILE_APP_LOGS_IDENTIFIERS.stream().anyMatch(identifier -> message.contains(identifier))
                ? MOBILE_APP_LOGGER
                : CREATIVE_LOGGER;
    }

    private static String dummyAccessToken(String pollingId) {
        return CUSTOM_LOG_ID_PREFIX + pollingId;
    }

    private static String generatePollingId() {
        return new BigInteger(NUMBER_OF_BITS_RANDOM_GENERATOR, SECURE_RANDOM).toString(RADIX);
    }
}
