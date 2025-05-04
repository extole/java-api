package com.extole.config.client.rest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.extole.client.rest.impl.ClientRestConfigMarker;
import com.extole.config.activity.service.ActivityConfigMarker;
import com.extole.config.client.consumer.event.service.ClientConsumerEventServiceBootstrapConfig;
import com.extole.config.common.rest.support.authorization.client.CommonRestClientAuthorizationSupportConfig;
import com.extole.config.consumer.event.producer.ConsumerEventProducerConfigMarker;
import com.extole.config.erasure.ErasureConfigMarker;
import com.extole.config.openid.connect.service.OpenIdConnectServiceConfigMarker;
import com.extole.config.optout.external.OptoutExternalConfigMarker;
import com.extole.config.rewards.RewardsBootstrapConfig;

@Configuration
@ComponentScan(
    basePackageClasses = {
        ConsumerEventProducerConfigMarker.class,
        RewardsBootstrapConfig.class,
        OpenIdConnectServiceConfigMarker.class,
        ActivityConfigMarker.class,
        OptoutExternalConfigMarker.class,
        ErasureConfigMarker.class,
        ClientRestConfigMarker.class,
        CommonRestClientAuthorizationSupportConfig.class,
        ClientConsumerEventServiceBootstrapConfig.class
    })
public class ClientRestConfig {

}
