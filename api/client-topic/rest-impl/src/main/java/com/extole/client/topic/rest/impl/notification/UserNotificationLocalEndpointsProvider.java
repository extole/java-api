package com.extole.client.topic.rest.impl.notification;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.client.topic.rest.UserNotificationLocalEndpoints;
import com.extole.common.rest.client.WebResourceFactory;
import com.extole.common.rest.client.exception.translation.extole.ExtoleRestExceptionTranslationStrategy;

@Component
public class UserNotificationLocalEndpointsProvider {

    private final List<UserNotificationLocalEndpoints> localEndpoints;

    @Autowired
    UserNotificationLocalEndpointsProvider(
        @Value("${notification.local.domains:api-0.${extole.environment:lo}.extole.io,"
            + "api-1.${extole.environment:lo}.extole.io}") String localDomainsAsString,
        @Qualifier("notificationApiClient") Client client) {
        List<String> localDomains = ImmutableList.copyOf(localDomainsAsString.split(","));
        ImmutableList.Builder<UserNotificationLocalEndpoints> localEndpointsListBuilder =
            ImmutableList.builder();
        for (String localDomain : localDomains) {
            WebTarget webTarget = client.target("https://" + localDomain);

            UserNotificationLocalEndpoints endpoints =
                WebResourceFactory.<UserNotificationLocalEndpoints>builder()
                    .withResourceInterface(UserNotificationLocalEndpoints.class)
                    .withTarget(webTarget)
                    .withRestExceptionTranslationStrategy(ExtoleRestExceptionTranslationStrategy.getSingleton())
                    .build();
            localEndpointsListBuilder.add(endpoints);
        }

        this.localEndpoints = localEndpointsListBuilder.build();
    }

    public List<UserNotificationLocalEndpoints> getLocalEndpoints() {
        return localEndpoints;
    }

}
