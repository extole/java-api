package com.extole.config.context.api;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.extole.api.impl.ContextApiConfigMarker;

@Configuration
@ComponentScan(basePackageClasses = {
    ContextApiConfigMarker.class
})
public class ContextApiConfig {

}
