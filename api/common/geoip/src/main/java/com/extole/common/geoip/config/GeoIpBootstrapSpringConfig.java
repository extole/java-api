package com.extole.common.geoip.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.extole.common.geoip"})
public interface GeoIpBootstrapSpringConfig {
}
