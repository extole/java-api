package com.extole.consumer.rest.impl.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.google.common.net.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.metrics.ExtoleCounter;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.metrics.ExtoleTimer;
import com.extole.common.rest.ExtoleHeaderType;
import com.extole.consumer.rest.core.CoreEndpoints;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.service.zone.ZoneRenderRequest;
import com.extole.id.Id;
import com.extole.id.IdGenerator;
import com.extole.model.entity.client.PublicClient;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;

@Provider
public class CoreEndpointsImpl implements CoreEndpoints {
    private static final IdGenerator ID_GENERATOR = new IdGenerator();
    private static final Logger LOG = LoggerFactory.getLogger(CoreEndpointsImpl.class);

    private final int maxAgeSeconds;
    private final HttpServletRequest servletRequest;
    private final CoreJsCache coreJsCache;
    private final ClientCache clientCache;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final ExtoleCounter countCoreJs;
    private final ExtoleCounter countCoreJsWithId;
    private final ExtoleCounter countCoreJsWithName;
    private final ExtoleTimer timerCoreJs;

    @Autowired
    public CoreEndpointsImpl(
        @Value("${consumer.corejs.cacheControl.maxAge.minutes:15}") int maxAgeMinutes,
        @Context HttpServletRequest servletRequest,
        ExtoleMetricRegistry metricRegistry,
        CoreJsCache coreArchiveCache,
        ClientCache clientCache,
        ConsumerRequestContextService consumerRequestContextService) {
        this.maxAgeSeconds = (int) TimeUnit.MINUTES.toSeconds(maxAgeMinutes);
        this.servletRequest = servletRequest;
        this.coreJsCache = coreArchiveCache;
        this.clientCache = clientCache;
        this.consumerRequestContextService = consumerRequestContextService;
        this.countCoreJs = metricRegistry.counter(CoreEndpoints.class + ".count.coreJs");
        this.countCoreJsWithId = metricRegistry.counter(CoreEndpoints.class + ".count.coreJsWithId");
        this.countCoreJsWithName = metricRegistry.counter(CoreEndpoints.class + ".count.coreJsWithPath");
        this.timerCoreJs = metricRegistry.timer(CoreEndpoints.class + ".timer.coreJs");
    }

    @Override
    public Response coreJs() {
        Id<ClientHandle> clientId = consumerRequestContextService.extractProgramDomain(servletRequest).getClientId();
        try {
            PublicClient client = clientCache.getById(clientId);
            return buildResponseForClientId(client, countCoreJs);
        } catch (ClientNotFoundException e) {
            LOG.debug("No client found for id {}", clientId, e);
            throw new NotFoundException(e);
        }
    }

    @Override
    public Response coreLoaderJs() {
        PublicProgram publicProgram = consumerRequestContextService.extractProgramDomain(servletRequest);
        String coreJsUrl = publicProgram.getScheme() + "://" + publicProgram.getProgramDomain().toString() + "/core.js";

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(maxAgeSeconds);
        return Response.ok("(function () {\n" +
            "    \"use strict\";\n" +
            "    function loadScript(url) {\n" +
            "        var script = document.createElement(\"script\");\n" +
            "        script.type = \"text/javascript\";\n" +
            "        script.src = url;\n" +
            "        document.head.appendChild(script);\n" +
            "    }\n" +
            "\n" +
            "    loadScript(\"" + coreJsUrl + "\");\n" +
            "})();\n", "application/javascript")
            .cacheControl(cacheControl)
            .header(HttpHeaders.ACCESS_CONTROL_MAX_AGE, Integer.valueOf(maxAgeSeconds))
            .build();
    }

    @Override
    public Response coreJsWithId(String clientId) {
        try {
            PublicClient client = clientCache.getById(Id.valueOf(clientId));
            return buildResponseForClientId(client, countCoreJsWithId);
        } catch (ClientNotFoundException e) {
            LOG.debug("No client found for id {}", clientId, e);
            throw new NotFoundException(e);
        }
    }

    @Override
    public Response coreJsWithName(String clientName) {
        try {
            PublicClient client = clientCache.getByShortName(clientName);
            return buildResponseForClientId(client, countCoreJsWithName);
        } catch (ClientNotFoundException e) {
            LOG.debug("No client found for name {}", clientName, e);
            throw new NotFoundException(e);
        }
    }

    @Override
    public Response coreJsWithIdWithPrefix(String clientId) {
        return coreJsWithId(clientId);
    }

    @Override
    public Response coreJsWithNameWithPrefix(String clientName) {
        return coreJsWithName(clientName);
    }

    private Response buildResponseForClientId(PublicClient client, ExtoleCounter metricCounter) {
        long startTime = System.currentTimeMillis();
        try {
            boolean isDebugRequest = isDebugRequest(servletRequest);
            String coreJs = coreJsCache.getCoreJs(client, !isDebugRequest);

            CacheControl cacheControl = new CacheControl();
            cacheControl.setMaxAge(maxAgeSeconds);

            return Response.ok(coreJs, "application/javascript")
                .cacheControl(cacheControl)
                .header(HttpHeaders.ACCESS_CONTROL_MAX_AGE, Integer.valueOf(maxAgeSeconds))
                .build();
        } catch (CoreJsUnavailableException e) {
            String message = ID_GENERATOR.generateId().toString() +
                "Failed to retrieve core.js for client " + client.getId();
            LOG.error(message, e);
            return Response.status(Status.SERVICE_UNAVAILABLE)
                .header(ExtoleHeaderType.ERROR_MESSAGE.getHeaderName(), '"' + message + '"')
                .build();
        } finally {
            timerCoreJs.update(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
            metricCounter.increment();
        }
    }

    private boolean isDebugRequest(HttpServletRequest request) {
        boolean isDebugParameter = request.getParameterMap().entrySet().stream()
            .filter(entry -> ZoneRenderRequest.ZONE_PARAMETER_DEBUG.equalsIgnoreCase(entry.getKey()))
            .anyMatch(entry -> Arrays.stream(entry.getValue())
                .anyMatch(value -> !Boolean.FALSE.toString().equalsIgnoreCase(value) && !"0".equals(value)));
        if (isDebugParameter) {
            return true;
        }

        boolean isDebugHeader = Collections.list(request.getHeaderNames()).stream()
            .filter(headerName -> ZoneRenderRequest.ZONE_HEADER_DEBUG.equalsIgnoreCase(headerName))
            .anyMatch(headerName -> Collections.list(request.getHeaders(headerName)).stream()
                .anyMatch(value -> !Boolean.FALSE.toString().equalsIgnoreCase(value) && !"0".equals(value)));
        return isDebugHeader;
    }
}
