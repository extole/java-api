package com.extole.config.common.rest.support.authorization.client;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.extole.config.authorization.client.AuthorizationClientSpringConfig;

@Configuration

@ComponentScan(basePackages = {
    "com.extole.common.rest.support.authorization.client"
},
    basePackageClasses = {
        AuthorizationClientSpringConfig.class
    })
public class CommonRestClientAuthorizationSupportConfig {
}
