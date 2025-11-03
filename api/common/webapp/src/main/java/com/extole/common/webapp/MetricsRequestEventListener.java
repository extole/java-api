package com.extole.common.webapp;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.codahale.metrics.Clock;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.uri.UriTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.rest.model.RequestContextAttributeName;

public final class MetricsRequestEventListener implements RequestEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsRequestEventListener.class);

    private static final Pattern MULTIPLE_SLASH_PATTERN = Pattern.compile("//+");
    private static final Pattern TRAILING_SLASH_PATTERN = Pattern.compile("/$");

    private final ExtoleMetricRegistry metricRegistry;
    private final String metricName;
    private final Clock clock;
    private final long start;
    private boolean requestMatched;
    private final List<String> detailedClientShortNames;
    private final List<CustomRequestMetricProvider> customRequestMetricProviders;

    public MetricsRequestEventListener(ExtoleMetricRegistry metricRegistry,
        String metricName, Clock clock, List<String> detailedClientShortNames,
        List<CustomRequestMetricProvider> customRequestMetricProviders) {
        this.metricRegistry = metricRegistry;
        this.metricName = metricName;
        this.clock = clock;
        this.start = clock.getTick();
        this.detailedClientShortNames = detailedClientShortNames;
        this.customRequestMetricProviders = customRequestMetricProviders;
    }

    @Override
    public void onEvent(RequestEvent event) {
        ContainerRequest containerRequest = event.getContainerRequest();

        switch (event.getType()) {
            case REQUEST_MATCHED:
                requestMatched = true;
                break;
            case FINISHED:
                if (requestMatched && event.getContainerResponse() != null) {
                    String uri = getUri(event);
                    String method = containerRequest.getMethod();
                    String statusCodeFamily = event.getContainerResponse().getStatusInfo().getFamily().toString();
                    String httpCode = String.valueOf(event.getContainerResponse().getStatus());

                    updateTimer(metricName, method, uri, statusCodeFamily, httpCode);
                    String clientShortName = (String) containerRequest
                        .getProperty(RequestContextAttributeName.CLIENT_SHORT_NAME.getAttributeName());
                    if (clientShortName != null) {
                        if (detailedClientShortNames.isEmpty() || detailedClientShortNames.contains(clientShortName)) {
                            updateTimer(metricName, method, uri, statusCodeFamily, httpCode, clientShortName);
                        }
                        updateTimer(metricName, method, statusCodeFamily, clientShortName);

                        customRequestMetricProviders.stream()
                            .filter(customRequestMetricProvider -> customRequestMetricProvider.shouldConsider(event))
                            .map(customRequestMetricProvider -> customRequestMetricProvider.computeMetricName(event,
                                metricName, method, uri, statusCodeFamily, httpCode, clientShortName))
                            .forEach(metricName -> updateTimer(metricName));
                    }
                } else {
                    ContainerResponse response = event.getContainerResponse();
                    LOG.warn("The unmatched request has been finished URL: {} {}, status: {}",
                        event.getUriInfo().getBaseUri().getPath(),
                        event.getUriInfo().getPath(),
                        response != null ? String.valueOf(response.getStatus()) : "no response");
                }
                break;
            default:
                break;
        }
    }

    private void updateTimer(String... metricNameParts) {
        metricRegistry.timer(String.join(".", metricNameParts)).update(clock.getTick() - start, TimeUnit.NANOSECONDS);
    }

    private String getUri(RequestEvent event) {
        ExtendedUriInfo uriInfo = event.getUriInfo();
        List<UriTemplate> templates = uriInfo.getMatchedTemplates();

        StringBuilder sb = new StringBuilder();
        for (int i = templates.size() - 1; i >= 0; i--) {
            sb.append(templates.get(i).getTemplate());
        }
        String multipleSlashCleaned = MULTIPLE_SLASH_PATTERN.matcher(sb.toString()).replaceAll("/");
        if (multipleSlashCleaned.equals("/")) {
            return multipleSlashCleaned;
        }
        String trailingSlashCleaned = TRAILING_SLASH_PATTERN.matcher(multipleSlashCleaned).replaceAll("");
        return trailingSlashCleaned;
    }
}
