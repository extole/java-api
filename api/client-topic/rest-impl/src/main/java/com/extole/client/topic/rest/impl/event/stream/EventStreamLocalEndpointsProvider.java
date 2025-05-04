package com.extole.client.topic.rest.impl.event.stream;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.client.topic.rest.event.stream.EventStreamLocalEndpoints;
import com.extole.common.rest.client.WebResourceFactory;
import com.extole.common.rest.client.exception.translation.extole.ExtoleRestExceptionTranslationStrategy;

@Component
public class EventStreamLocalEndpointsProvider {

    private final List<EventStreamLocalEndpoints> localEndpoints;

    @Autowired
    EventStreamLocalEndpointsProvider(
        @Value("${event.stream.local.domains:api-0.${extole.environment:lo}.extole.io,"
            + "api-1.${extole.environment:lo}.extole.io}") String localDomainsAsString,
        @Qualifier("eventStreamApiClient") Client client) {
        List<String> localDomains = ImmutableList.copyOf(localDomainsAsString.split(","));
        ImmutableList.Builder<EventStreamLocalEndpoints> localEndpointsListBuilder =
            ImmutableList.builder();
        for (String localDomain : localDomains) {
            WebTarget webTarget = client.target("https://" + localDomain);

            EventStreamLocalEndpoints endpoints =
                WebResourceFactory.<EventStreamLocalEndpoints>builder()
                    .withResourceInterface(EventStreamLocalEndpoints.class)
                    .withTarget(webTarget)
                    .withRestExceptionTranslationStrategy(ExtoleRestExceptionTranslationStrategy.getSingleton())
                    .build();
            localEndpointsListBuilder.add(endpoints);
        }

        this.localEndpoints = localEndpointsListBuilder.build();
    }

    public List<EventStreamLocalEndpoints> getLocalEndpoints() {
        return localEndpoints;
    }

}
