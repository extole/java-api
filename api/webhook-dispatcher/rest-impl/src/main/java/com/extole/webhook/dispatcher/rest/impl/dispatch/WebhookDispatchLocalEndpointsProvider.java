package com.extole.webhook.dispatcher.rest.impl.dispatch;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.rest.client.WebResourceFactory;
import com.extole.common.rest.client.exception.translation.extole.ExtoleRestExceptionTranslationStrategy;
import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchLocalEndpoints;

@Component
public class WebhookDispatchLocalEndpointsProvider {

    private final List<WebhookDispatchLocalEndpoints> localEndpoints;

    @Autowired
    WebhookDispatchLocalEndpointsProvider(
        @Value("${webhook.local.domains:api-0.${extole.environment:lo}.extole.io,"
            + "api-1.${extole.environment:lo}.extole.io}") String localDomainsAsString,
        @Qualifier("webhookDispatcherApiClient") Client client) {
        List<String> localDomains = ImmutableList.copyOf(localDomainsAsString.split(","));
        ImmutableList.Builder<WebhookDispatchLocalEndpoints> localEndpointsListBuilder =
            ImmutableList.builder();
        for (String localDomain : localDomains) {
            WebTarget webTarget = client.target("https://" + localDomain);

            WebhookDispatchLocalEndpoints endpoints =
                WebResourceFactory.<WebhookDispatchLocalEndpoints>builder()
                    .withResourceInterface(WebhookDispatchLocalEndpoints.class)
                    .withTarget(webTarget)
                    .withRestExceptionTranslationStrategy(ExtoleRestExceptionTranslationStrategy.getSingleton())
                    .build();
            localEndpointsListBuilder.add(endpoints);
        }

        this.localEndpoints = localEndpointsListBuilder.build();
    }

    public List<WebhookDispatchLocalEndpoints> getLocalEndpoints() {
        return localEndpoints;
    }

}
