package com.extole.client.zone.rest.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.extole.client.zone.rest.ClientZonesEndpoints;
import com.extole.common.lang.ExtoleThreadFactory;
import com.extole.id.IdGenerator;

@Component
public class TurbotaxRequestTimeHealthCheck extends HealthCheck {

    private static final Logger LOG = LoggerFactory.getLogger(TurbotaxRequestTimeHealthCheck.class);
    private static final String TURBOTAX_TRACING_MONITOR_TOKEN = "5E0TPPG0A22GASDI8PE2GBMPDF";

    private static final String LOCALHOST_ZONES_URL = "http://localhost:8080/client-zone-api/api" + getZonesEndpoint();
    private static final String GET_OR_CREATE_CODE_ZONE_NAME = "get_or_create_code";
    private static final String REQUEST_BODY = "{\n" +
        "  \"sandbox\": \"production-test\",\n" +
        "  \"labels\": \"turbotax-refer-a-friend\",\n" +
        "  \"email\": \"test%s@extole.com\",\n" +
        "  \"partner_user_id\": \"%s\",\n" +
        "  \"requested_share_code\": null,\n" +
        "  \"user_data\": {},\n" +
        "  \"share_code_data\": {\n" +
        "    \"dynamic_data\": \"9VVCMj3vZyw353H0nzPx2w==\",\n" +
        "    \"cid\": \"soc_frb_invtfr_raf_prd_nav_pf_ty24\"\n" +
        "  }\n" +
        "}";

    private static final int ONE_THOUSAND = 1000;
    private static final int MAX_TOTAL_ATTEMPTS = 10;
    private static final int MIN_SUCCESSFUL_ATTEMPTS = 3;

    private final IdGenerator idGenerator = new IdGenerator();
    private final String environment;
    private final HttpClient httpClient;
    private final ExecutorService tracingExecutor;

    @Autowired
    public TurbotaxRequestTimeHealthCheck(
        @Value("${extole.environment:lo}") String environment) {
        this.environment = environment;
        this.httpClient = HttpClient.newBuilder().build();
        this.tracingExecutor = Executors.newSingleThreadExecutor(
            new ExtoleThreadFactory("turbotax-healthcheck-trace"));
    }

    @Override
    protected Result check() {
        if (environment.equals("pr")) {
            tracingExecutor.submit(() -> sendTurbotaxTracingRequest());
        }
        return Result.healthy();
    }

    private void sendTurbotaxTracingRequest() {
        try {

            int successfulAttempts = 0;
            int totalAttempts = 0;

            Stopwatch totalStopwatch = Stopwatch.createStarted();
            while (totalAttempts < MAX_TOTAL_ATTEMPTS) {
                HttpRequest turbotaxClientZoneRequest = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers
                        .ofString(String.format(REQUEST_BODY, idGenerator.generateId().getValue(),
                            idGenerator.generateId().getValue())))
                    .uri(URI.create(LOCALHOST_ZONES_URL + "/" + GET_OR_CREATE_CODE_ZONE_NAME))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + TURBOTAX_TRACING_MONITOR_TOKEN)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .build();

                Stopwatch stopwatch = Stopwatch.createStarted();
                HttpResponse<String> response =
                    httpClient.send(turbotaxClientZoneRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != HttpStatus.OK.value()) {
                    LOG.error("Response code should be 200 if webapp is healthy. Actual response: {}",
                        Integer.valueOf(response.statusCode()));
                    break;
                }
                stopwatch.stop();

                if (stopwatch.elapsed().toMillis() < ONE_THOUSAND) {
                    successfulAttempts++;
                    if (successfulAttempts >= MIN_SUCCESSFUL_ATTEMPTS) {
                        LOG.warn("Healthy TurboTax cache priming check took {}ms",
                            Long.valueOf(totalStopwatch.elapsed().toMillis()));
                        break;
                    }
                }
                totalAttempts++;
            }
        } catch (Exception e) {
            LOG.error("Could not check priming for TurboTax", e);
        }
    }

    private static String getZonesEndpoint() {
        Path pathAnnotation = ClientZonesEndpoints.class.getAnnotation(Path.class);
        if (pathAnnotation == null) {
            return StringUtils.EMPTY;
        }
        return pathAnnotation.value();
    }
}
