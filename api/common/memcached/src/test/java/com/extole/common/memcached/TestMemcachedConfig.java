package com.extole.common.memcached;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.spring.ServiceLocator;

@Configuration
@ComponentScan(basePackages = {"com.extole.common.memcached"})
public class TestMemcachedConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ExtoleMetricRegistry getMetricRegistry() {
        ExtoleMetricRegistry metricRegistry = new ExtoleMetricRegistry(new MetricRegistry());
        metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
        metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
        metricRegistry.register("jvm.thread-states", new ThreadStatesGaugeSet());
        metricRegistry.register("jvm.fd.usage", new FileDescriptorRatioGauge());
        return metricRegistry;
    }

    @Bean
    public ServiceLocator serviceLocator() {
        return new ServiceLocator();
    }
}
