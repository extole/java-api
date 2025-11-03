package com.extole.common.memcached.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.extole.common.memcached.ExtoleMemcachedClient;
import com.extole.common.memcached.ExtoleMemcachedFactory;
import com.extole.common.memcached.JsonMemcachedTransformerFactory;
import com.extole.common.metrics.ExtoleMetricRegistry;

@Configuration
public class MemcachedLockConfiguration {
    private static final String STORE_NAME = "lock";

    @Value("${extole.instance.name:lo}")
    private String instanceName;

    @Value("${lock.poll.min.sleep.ms:5}")
    private int pollingMinSleepTimeMs;

    @Value("${lock.attempts:40}")
    private int maxLockAttempts;

    @Autowired
    private ExtoleMetricRegistry extoleMetricRegistry;
    @Autowired
    private JsonMemcachedTransformerFactory jsonMemcachedTransformerFactory;
    @Autowired
    private ExtoleMemcachedFactory extoleMemcachedFactory;

    @Bean
    public MemcachedLockManager memcachedLockManager() {
        ExtoleMemcachedClient<String, LockPojo> client =
            extoleMemcachedFactory.create().initialize()
                .build(STORE_NAME, jsonMemcachedTransformerFactory.createBinaryTransformer(STORE_NAME, LockPojo.class));

        MemcachedLockManager lockManager = new MemcachedLockManagerImpl(instanceName, pollingMinSleepTimeMs,
            maxLockAttempts, extoleMetricRegistry, client);

        return lockManager;
    }
}
