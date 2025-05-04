package com.extole.common.rest.exception;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class RestExceptionLogger {

    private static final String DEBUG_REQUEST_HEADER = "X-Extole-Debug";
    private static final String DEBUG_REQUEST_PARAMETER = "debug";

    private static final int HTTP_STATUS_500 = 500;

    private static final String LOGGER_FORMAT_WITH_REQUEST =
        "Unique ID: {}, HTTP {}: {}, Parameters: {}, Request Method: {}, Request Url: {}";
    private static final String LOGGER_FORMAT_WITH_REQUEST_AND_POD =
        "Unique ID: {}, HTTP {}: {}, Parameters: {}, Request Method: {}, Request Url: {}, Pod: {}";
    private static final String LOGGER_FORMAT_WITH_REQUEST_NO_STACKTRACE =
        "Unique ID: {}, HTTP {}: {}, Parameters: {}, Request Method: {}, Request Url: {}\n\tCause: {}";
    private static final String LOGGER_FORMAT_WITH_REQUEST_AND_POD_NO_STACKTRACE =
        "Unique ID: {}, HTTP {}: {}, Parameters: {}, Request Method: {}, Request Url: {}, Pod: {}\n\tCause: {}";
    private final Logger log;

    public RestExceptionLogger(Logger logger) {
        this.log = logger;
    }

    public void log(RestException exception, HttpServletRequest request) {
        log(exception.getUniqueId(), Integer.valueOf(exception.getHttpStatusCode()),
            exception.getMessage(), exception.getParameters(), exception, request, Optional.empty());
    }

    public void log(RestException exception, HttpServletRequest request, String pod) {
        log(exception.getUniqueId(), Integer.valueOf(exception.getHttpStatusCode()),
            exception.getMessage(), exception.getParameters(), exception, request, Optional.ofNullable(pod));
    }

    private void log(String uniqueId, Integer httpStatus, String message, Map<String, Object> parameters,
        RestException exception, HttpServletRequest request, Optional<String> pod) {
        String requestUrl = "";
        String requestMethod = request.getMethod();
        if (request != null) {
            requestUrl = request.getQueryString() == null ? request.getRequestURL().toString()
                : request.getRequestURL().append("?").append(request.getQueryString()).toString();
        }

        boolean logStacktrace = log.isDebugEnabled() || log.isTraceEnabled() || isDebugRequest(request);

        String logPattern = LOGGER_FORMAT_WITH_REQUEST;
        Object[] arguments = List.of(uniqueId, httpStatus, message, parameters, requestMethod, requestUrl).toArray();
        if (pod.isPresent()) {
            logPattern = LOGGER_FORMAT_WITH_REQUEST_AND_POD;
            arguments = ArrayUtils.add(arguments, pod.get());
        }
        if (logStacktrace || httpStatus.intValue() >= HTTP_STATUS_500) {
            arguments = ArrayUtils.add(arguments, exception);
        } else {
            if (pod.isEmpty()) {
                logPattern = LOGGER_FORMAT_WITH_REQUEST_NO_STACKTRACE;
            } else {
                logPattern = LOGGER_FORMAT_WITH_REQUEST_AND_POD_NO_STACKTRACE;
            }
            arguments = ArrayUtils.add(arguments, exception.getMessage());
        }

        if (httpStatus.intValue() < HTTP_STATUS_500) {
            log.warn(logPattern, arguments);
        } else {
            log.error(logPattern, arguments);
        }
    }

    private static boolean isDebugRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        boolean isDebugParameter = request.getParameterMap().entrySet().stream()
            .filter(entry -> DEBUG_REQUEST_PARAMETER.equalsIgnoreCase(entry.getKey()))
            .flatMap(entry -> Arrays.stream(entry.getValue()))
            .anyMatch(value -> !Boolean.FALSE.toString().equalsIgnoreCase(value) && !"0".equals(value));
        if (isDebugParameter) {
            return true;
        }

        return Collections.list(request.getHeaders(DEBUG_REQUEST_HEADER)).stream()
            .anyMatch(value -> !Boolean.FALSE.toString().equalsIgnoreCase(value) && !"0".equals(value));
    }
}
