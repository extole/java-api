package com.extole.client.rest.impl.memcached;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.memcached.ExtoleMemcachedClient;
import com.extole.common.memcached.ExtoleMemcachedClientBuilder;
import com.extole.common.memcached.ExtoleMemcachedFactory;
import com.extole.common.memcached.InvalidMemcachedExpirationException;
import com.extole.common.memcached.MemcachedExpiration;
import com.extole.common.memcached.StringMemcachedTransformer;
import com.extole.common.metrics.ExtoleMetricRegistry;

@Component
public class TestCreativeMemcachedFactory {
    private final ExtoleMemcachedClientBuilder memcachedClientBuilder;
    private final ExtoleMetricRegistry metricRegistry;

    @Autowired
    public TestCreativeMemcachedFactory(
        @Value("${assembled.archive.memcached.servers:memcached-creative.${extole.environment:lo}.intole.net:11211}")
        String memcachedServers,
        @Value("${assembled.archive.memcached.itemMaxSize:3145728}") int itemMaxSize,
        @Value("${assembled.archive.memcached.expiration.days:7}") int expirationInDays,
        ExtoleMemcachedFactory extoleMemcachedFactory,
        ExtoleMetricRegistry metricRegistry) throws InvalidMemcachedExpirationException {

        this.metricRegistry = metricRegistry;
        memcachedClientBuilder = extoleMemcachedFactory.create()
            .withServers(memcachedServers)
            .withExpiration(new MemcachedExpiration(Duration.ofDays(expirationInDays)))
            .withItemMaxSize(itemMaxSize)
            .initialize();
    }

    public ExtoleMemcachedClient<String, String> createMemcachedClient(String storeName) {
        return memcachedClientBuilder.build(storeName, new StringMemcachedTransformer(metricRegistry, storeName));
    }
}
