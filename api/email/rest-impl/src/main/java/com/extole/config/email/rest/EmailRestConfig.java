package com.extole.config.email.rest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.extole.email.rest.impl.authorization")
public class EmailRestConfig {
}
