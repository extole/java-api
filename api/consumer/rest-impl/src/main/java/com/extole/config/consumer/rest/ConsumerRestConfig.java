package com.extole.config.consumer.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.extole.config.actions.ActionConfigMarker;
import com.extole.config.common.rest.support.authorization.person.CommonRestPersonAuthorizationSupportConfig;
import com.extole.config.key.provider.KeyProviderServiceConfigMarker;
import com.extole.config.optout.external.OptoutExternalConfigMarker;
import com.extole.consumer.rest.impl.ConsumerRestPackageMarker;
import com.extole.event.report.done.ReportDoneConfigMarker;
import com.extole.google.api.GoogleApiConfigMarker;
import com.extole.model.service.config.ModelBootstrapSpringConfig;
import com.extole.optout.client.OptoutClientConfigMarker;

@Configuration
@ComponentScan(basePackageClasses = {
    ConsumerRestPackageMarker.class,
    ModelBootstrapSpringConfig.class,
    ReportDoneConfigMarker.class,
    OptoutExternalConfigMarker.class,
    ActionConfigMarker.class,
    CommonRestPersonAuthorizationSupportConfig.class,
    OptoutClientConfigMarker.class,
    GoogleApiConfigMarker.class,
    KeyProviderServiceConfigMarker.class
},
    basePackages = {"com.extole.common.security"})
public class ConsumerRestConfig {

    @Bean
    public PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
