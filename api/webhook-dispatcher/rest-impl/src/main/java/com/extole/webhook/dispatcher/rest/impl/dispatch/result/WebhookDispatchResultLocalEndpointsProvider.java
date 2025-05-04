package com.extole.webhook.dispatcher.rest.impl.dispatch.result;

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
import com.extole.webhook.dispatcher.rest.dispatch.result.WebhookDispatchResultLocalEndpoints;

@Component
class WebhookDispatchResultLocalEndpointsProvider {

    private final List<WebhookDispatchResultLocalEndpoints> localEndpoints;

    @Autowired
    WebhookDispatchResultLocalEndpointsProvider(
        @Value("${webhook.local.domains:api-0.${extole.environment:lo}.extole.io,"
            + "api-1.${extole.environment:lo}.extole.io}") String localDomainsAsString,
        @Qualifier("webhookDispatcherApiClient") Client client) {
        List<String> localDomains = ImmutableList.copyOf(localDomainsAsString.split(","));
        ImmutableList.Builder<WebhookDispatchResultLocalEndpoints> localEndpointsListBuilder =
            ImmutableList.builder();
        for (String localDomain : localDomains) {
            WebTarget webTarget = client.target("https://" + localDomain);

            WebhookDispatchResultLocalEndpoints endpoints =
                WebResourceFactory.<WebhookDispatchResultLocalEndpoints>builder()
                    .withResourceInterface(WebhookDispatchResultLocalEndpoints.class)
                    .withTarget(webTarget)
                    .withRestExceptionTranslationStrategy(ExtoleRestExceptionTranslationStrategy.getSingleton())
                    .build();
            localEndpointsListBuilder.add(endpoints);
        }

        this.localEndpoints = localEndpointsListBuilder.build();
    }

    public List<WebhookDispatchResultLocalEndpoints> getClusterEndpoints() {
        return localEndpoints;
    }

}
