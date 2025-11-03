package com.extole.consumer.rest.impl.debug;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public final class HttpUserAgentExtractor {

    private static final HttpUserAgentExtractor INSTANCE = new HttpUserAgentExtractor();

    private static final List<String> USER_AGENT_HEADER_NAMES = List.of("User-Agent", "user_agent", "http_user_agent");

    private HttpUserAgentExtractor() {
    }

    public Optional<String> getUserAgent(HttpServletRequest servletRequest) {
        Optional<String> userAgent = Optional.empty();
        for (String headerName : USER_AGENT_HEADER_NAMES) {
            String headerValue = servletRequest.getHeader(headerName);
            if (StringUtils.isNotBlank(headerValue)) {
                userAgent = Optional.of(headerValue);
                break;
            }
        }
        return userAgent;
    }

    public static HttpUserAgentExtractor getInstance() {
        return INSTANCE;
    }
}
