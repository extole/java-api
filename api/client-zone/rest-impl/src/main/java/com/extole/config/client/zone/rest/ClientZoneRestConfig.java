package com.extole.config.client.zone.rest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.extole.client.zone.rest.impl.ClientZoneRestPackageMarker;
import com.extole.config.client.consumer.event.service.ClientConsumerEventServiceBootstrapConfig;
import com.extole.config.common.rest.support.authorization.client.CommonRestClientAuthorizationSupportConfig;
import com.extole.config.consumer.creative.ConsumerCreativeConfig;
import com.extole.config.creative.CreativeAssemblerSpringConfig;
import com.extole.model.service.config.ModelBootstrapSpringConfig;

@Configuration
@ComponentScan(
    basePackageClasses = {
        ClientZoneRestPackageMarker.class,
        ModelBootstrapSpringConfig.class,
        CommonRestClientAuthorizationSupportConfig.class,
        ClientConsumerEventServiceBootstrapConfig.class,
        ConsumerCreativeConfig.class,
        CreativeAssemblerSpringConfig.class
    })
public class ClientZoneRestConfig implements ClientZoneRestConfigMarker {

}
