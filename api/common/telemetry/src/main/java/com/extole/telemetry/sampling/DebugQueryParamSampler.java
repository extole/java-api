package com.extole.telemetry.sampling;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;

public class DebugQueryParamSampler implements Sampler {

    private static final Logger LOGGER = Logger.getLogger(DebugQueryParamSampler.class.getName());
    private static final String HTTP_TARGET_ATTRIBUTE_NAME = "http.target";
    private static final String QUERY_PARAM_ONE = "x-extole-debug";
    private static final String QUERY_PARAM_TWO = "debug";

    @Override
    public SamplingResult shouldSample(Context parentContext, String traceId, String name, SpanKind spanKind,
        Attributes attributes, List<LinkData> parentLinks) {
        boolean shouldSample = shouldSample(attributes);
        if (shouldSample) {
            LOGGER.info(String.format("DebugQueryParamSampler: Sampling ENABLED for traceId=%s", traceId));
        }
        return shouldSample ? SamplingResult.recordAndSample() : SamplingResult.drop();
    }

    private static boolean shouldSample(Attributes attributes) {
        String url = attributes.get(AttributeKey.stringKey(HTTP_TARGET_ATTRIBUTE_NAME));
        if (url == null) {
            return false;
        }

        Map<String, String> queryParameters = getQueryParameters(url);
        return Boolean.parseBoolean(queryParameters.get(QUERY_PARAM_ONE)) ||
            Boolean.parseBoolean(queryParameters.get(QUERY_PARAM_TWO));
    }

    @Override
    public String getDescription() {
        return "Extole custom OpenTelemetry sampler that forces sampling based on debug query parameters";
    }

    public static Map<String, String> getQueryParameters(String url) {
        Map<String, String> queryPairs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        try {
            URI uri = new URI(url);
            String query = uri.getRawQuery();
            if (query != null) {
                for (String pair : query.split("&")) {
                    int index = pair.indexOf("=");
                    String key = URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(index + 1), StandardCharsets.UTF_8);
                    queryPairs.putIfAbsent(key, value);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL or query string: " + url, e);
        }
        return queryPairs;
    }

}
