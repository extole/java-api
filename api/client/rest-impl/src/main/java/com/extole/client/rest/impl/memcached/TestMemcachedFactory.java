package com.extole.client.rest.impl.memcached;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.common.memcached.ExtoleMemcachedClient;
import com.extole.common.memcached.ExtoleMemcachedClientBuilder;
import com.extole.common.memcached.ExtoleMemcachedFactory;
import com.extole.common.memcached.JsonMemcachedTransformerFactory;
import com.extole.common.memcached.StringMemcachedTransformer;
import com.extole.common.metrics.ExtoleMetricRegistry;

@Component
public class TestMemcachedFactory {
    private final JsonMemcachedTransformerFactory jsonMemcachedTransformerFactory;
    private final ExtoleMemcachedClientBuilder memcachedClientBuilder;
    private final ExtoleMetricRegistry metricRegistry;

    @Autowired
    public TestMemcachedFactory(
        JsonMemcachedTransformerFactory jsonMemcachedTransformerFactory,
        ExtoleMemcachedFactory extoleMemcachedFactory, ExtoleMetricRegistry metricRegistry) {
        this.jsonMemcachedTransformerFactory = jsonMemcachedTransformerFactory;
        this.metricRegistry = metricRegistry;
        this.memcachedClientBuilder = extoleMemcachedFactory.create().initialize();
    }

    public ExtoleMemcachedClient<String, String> createMemcachedClient(String storeName) {
        return memcachedClientBuilder.build(storeName, new StringMemcachedTransformer(metricRegistry, storeName));
    }

    public <T> ExtoleMemcachedClient<String, T> createMemcachedClient(String storeName, Class<T> valueClass) {
        return memcachedClientBuilder.build(storeName,
            jsonMemcachedTransformerFactory.createBinaryTransformer(storeName, valueClass));
    }
}
