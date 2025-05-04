package com.extole.config.common.rest.support.authorization.person;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.extole.config.authorization.person.AuthorizationPersonSpringConfig;

@Configuration

@ComponentScan(basePackages = {
    "com.extole.common.rest.support.authorization.person"
},
    basePackageClasses = {
        AuthorizationPersonSpringConfig.class
    })
public class CommonRestPersonAuthorizationSupportConfig {
}
