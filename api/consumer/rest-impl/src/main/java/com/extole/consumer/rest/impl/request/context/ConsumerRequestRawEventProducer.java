package com.extole.consumer.rest.impl.request.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.support.filter.CachedBodyHttpServletRequestWrapper;
import com.extole.common.rest.support.header.SourceIpProcessor;
import com.extole.event.consumer.ApiType;
import com.extole.event.consumer.ClientDomainContext;
import com.extole.event.consumer.raw.HttpRequestMethod;
import com.extole.event.consumer.raw.RawEventBuildResult;
import com.extole.event.consumer.raw.RawEventProducer;
import com.extole.event.consumer.raw.RawEventProducer.RawEventBuilder;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;

@Component
public class ConsumerRequestRawEventProducer {

    private final int maxAllowedBodySizeBytes;
    private final RawEventProducer rawEventProducer;
    private final SourceIpProcessor sourceIpProcessor;

    @Autowired
    public ConsumerRequestRawEventProducer(
        @Value("${consumer.raw-event.body.size.max.bytes:102400}") int maxAllowedBodySizeBytes,
        RawEventProducer rawEventProducer, SourceIpProcessor sourceIpProcessor) {
        this.maxAllowedBodySizeBytes = maxAllowedBodySizeBytes;
        this.rawEventProducer = rawEventProducer;
        this.sourceIpProcessor = sourceIpProcessor;
    }

    public RawEventBuildResult buildRawEvent(HttpServletRequest servletRequest, Optional<UriInfo> uriInfo,
        Optional<HttpHeaders> httpHeaders, PublicProgram program, String eventName,
        ConsumerRequestContextService.HttpRequestBodyCapturingType requestBodyCapturingType)
        throws RequestBodySizeTooLargeException {
        Multimap<String, String> httpParametersMap =
            uriInfo.isPresent() ? getHttpParameters(uriInfo.get()) : getHttpParameters(servletRequest);
        Multimap<String, String> httpHeadersMap =
            httpHeaders.isPresent() ? getHttpHeaders(httpHeaders.get()) : getHttpHeaders(servletRequest);
        Multimap<String, String> httpCookiesMap =
            httpHeaders.isPresent() ? getHttpCookies(httpHeaders.get()) : getHttpCookies(servletRequest);
        return buildRawEvent(servletRequest, program, eventName, httpParametersMap, httpHeadersMap, httpCookiesMap,
            requestBodyCapturingType);
    }

    private RawEventBuildResult buildRawEvent(HttpServletRequest servletRequest, PublicProgram program,
        String eventName, Multimap<String, String> httpParameters, Multimap<String, String> httpHeaders,
        Multimap<String, String> httpCookies,
        ConsumerRequestContextService.HttpRequestBodyCapturingType requestBodyCapturingType)
        throws RequestBodySizeTooLargeException {
        List<String> logMessages = new ArrayList<>();
        String rawEventUrl = getRawEventUrl(servletRequest);

        RawEventBuilder rawEventBuilder = rawEventProducer.createBuilder()
            .withApiType(ApiType.CONSUMER)
            .withEventName(eventName)
            .withHttpParameters(httpParameters)
            .withHttpHeaders(httpHeaders)
            .withHttpCookies(httpCookies)
            .withSourceIps(sourceIpProcessor.readSourceIps(servletRequest))
            .withUrl(rawEventUrl)
            .withClientDomainContext(
                new ClientDomainContext(program.getProgramDomain().toString(), program.getId()))
            .withHttpRequestMethod(
                servletRequest.getMethod() != null ? HttpRequestMethod.parse(servletRequest.getMethod())
                    : HttpRequestMethod.UNKNOWN);

        Optional<String> referrer = httpHeaders.get(com.google.common.net.HttpHeaders.REFERER.toLowerCase())
            .stream().filter(value -> !Strings.isNullOrEmpty(value)).findFirst();
        referrer.ifPresent(value -> rawEventBuilder.withReferrer(value));

        byte[] httpRequestBody = getAndValidateRequestBody(servletRequest, eventName, program.getClientId(),
            requestBodyCapturingType, logMessages, rawEventUrl);
        if (ArrayUtils.isNotEmpty(httpRequestBody)) {
            rawEventBuilder.withHttpRequestBody(httpRequestBody);
        }

        return new RawEventBuildResult(rawEventBuilder.build(), logMessages);
    }

    private String getRawEventUrl(HttpServletRequest servletRequest) {
        String incomingUrlFromHeaders = servletRequest.getHeader("X-Extole-Incoming-Url");

        if (StringUtils.isNotBlank(incomingUrlFromHeaders)) {
            return incomingUrlFromHeaders;
        }

        return servletRequest.getRequestURL().toString();
    }

    private byte[] getAndValidateRequestBody(HttpServletRequest servletRequest, String eventName,
        Id<ClientHandle> clientId, ConsumerRequestContextService.HttpRequestBodyCapturingType requestBodyCapturingType,
        List<String> logMessages, String rawEventUrl)
        throws RequestBodySizeTooLargeException {

        CachedBodyHttpServletRequestWrapper request = (CachedBodyHttpServletRequestWrapper) servletRequest;
        byte[] requestBodyBytes = request.getHttpRequestBody();
        int bodySizeBytes = requestBodyBytes.length;

        if (bodySizeBytes > maxAllowedBodySizeBytes) {
            if (requestBodyCapturingType != ConsumerRequestContextService.HttpRequestBodyCapturingType.LIMITED) {
                String message = "Request from clientId: " + clientId + ", on url: " + rawEventUrl
                    + ", with request method: " + request.getMethod() + ", and event name: " + eventName
                    + ", has the request body larger than accepted.";
                throw new RequestBodySizeTooLargeException(message, bodySizeBytes, maxAllowedBodySizeBytes);
            }
            requestBodyBytes = Arrays.copyOf(requestBodyBytes, maxAllowedBodySizeBytes);
            logMessages.add("Http request body was trimmed from: " + bodySizeBytes + " bytes, to: "
                + maxAllowedBodySizeBytes + " bytes");
        }
        return requestBodyBytes;
    }

    private Multimap<String, String> getHttpParameters(HttpServletRequest servletRequest) {
        Multimap<String, String> parametersMap = ArrayListMultimap.create();
        Map<String, String[]> parameters = servletRequest.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            return parametersMap;
        }
        parameters.keySet().stream().forEach(key -> {
            String[] values = parameters.get(key);
            if (values != null && values.length > 0 && !Strings.isNullOrEmpty(values[0])) {
                parametersMap.putAll(key, Lists.newArrayList(values));
            }
        });
        return parametersMap;
    }

    private Multimap<String, String> getHttpParameters(UriInfo uriInfo) {
        Multimap<String, String> result = ArrayListMultimap.create();
        for (Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            result.putAll(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private Multimap<String, String> getHttpHeaders(HttpServletRequest servletRequest) {
        Multimap<String, String> headers = ArrayListMultimap.create();
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            Enumeration<String> headerValues = servletRequest.getHeaders(name);
            while (headerValues.hasMoreElements()) {
                headers.put(name, headerValues.nextElement());
            }
        }
        return headers;
    }

    private Multimap<String, String> getHttpHeaders(HttpHeaders httpHeaders) {
        Multimap<String, String> result = ArrayListMultimap.create();
        for (Map.Entry<String, List<String>> entry : httpHeaders.getRequestHeaders().entrySet()) {
            result.putAll(entry.getKey().toLowerCase(), entry.getValue());
        }
        return result;
    }

    private Multimap<String, String> getHttpCookies(HttpServletRequest servletRequest) {
        Multimap<String, String> cookiesMap = ArrayListMultimap.create();
        if (servletRequest.getCookies() == null || servletRequest.getCookies().length == 0) {
            return cookiesMap;
        }
        Arrays.stream(servletRequest.getCookies())
            .forEach(cookie -> cookiesMap.put(cookie.getName(), cookie.getValue()));
        return cookiesMap;
    }

    private Multimap<String, String> getHttpCookies(HttpHeaders httpHeaders) {
        Multimap<String, String> cookiesMap = ArrayListMultimap.create();
        for (Map.Entry<String, Cookie> entry : httpHeaders.getCookies().entrySet()) {
            cookiesMap.put(entry.getKey(), entry.getValue().getValue());
        }
        return cookiesMap;
    }
}
