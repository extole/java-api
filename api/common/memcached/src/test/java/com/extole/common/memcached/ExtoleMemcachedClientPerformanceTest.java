package com.extole.common.memcached;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Disabled("for manual execution")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestMemcachedConfig.class})
public class ExtoleMemcachedClientPerformanceTest {
    private static final String STORE_NAME = "testStorePerformance";
    private static final String YOUR_MEMCACHED_SERVERS = "azatest.9u8bpv.cfg.use2.cache.amazonaws.com:11211";
    private static final int CLIENT_POOL_SIZE = 1;

    @Autowired
    private ExtoleMemcachedFactory extoleMemcachedFactory;

    @Autowired
    private JsonMemcachedTransformerFactory jsonTransformerFactory;

    private ExtoleMemcachedClient<String, String> memcachedClient;

    @BeforeEach
    public void setup() {
        memcachedClient = extoleMemcachedFactory.create()
            .withServers(YOUR_MEMCACHED_SERVERS)
            .withClientPoolSize(CLIENT_POOL_SIZE)
            .initialize()
            .build(STORE_NAME, jsonTransformerFactory.createBinaryTransformer(STORE_NAME, String.class));
    }

    @Test
    public void testMemcachedPerformance() throws Exception {
        Instant beginSetup = Instant.now();
        int threadCount = 50;
        int totalSetGetOperations = 20000;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger misses = new AtomicInteger(0);
        AtomicInteger writeExceptions = new AtomicInteger(0);
        class SetAndGetMemcachedKey implements Runnable {
            private final String key;

            SetAndGetMemcachedKey(String key) {
                this.key = key;
            }

            @Override
            public void run() {
                try {
                    memcachedClient.set(key, "{\"PERSON_ID\":123,\"IDENTITY_ID\":null,\"JOURNEYS\":[],\"VERSION\":1}");
                } catch (ExtoleMemcachedException | InterruptedException e) {
                    writeExceptions.incrementAndGet();
                }
                Optional<String> getResult = Optional.empty();
                try {
                    getResult = memcachedClient.get(key);
                } catch (ExtoleMemcachedException | InterruptedException e) {
                    misses.incrementAndGet();
                }
                if (!getResult.isPresent()) {
                    misses.incrementAndGet();
                }
                getResult = Optional.empty();
                try {
                    getResult = memcachedClient.get(key);
                } catch (ExtoleMemcachedException | InterruptedException e) {
                    misses.incrementAndGet();
                }
                if (!getResult.isPresent()) {
                    misses.incrementAndGet();
                }
            }
        }
        Instant beginTest = Instant.now();
        Long setupDuration = Long.valueOf(Duration.between(beginSetup, beginTest).toMillis());
        List<Future<?>> results = new ArrayList<>();
        for (int i = 0; i <= totalSetGetOperations; i++) {
            String key = i + "key";
            results.add(executorService.submit(new SetAndGetMemcachedKey(key)));
        }
        for (Future<?> result : results) {
            result.get();
        }
        executorService.shutdown();
        Long testDuration = Long.valueOf(Duration.between(beginTest, Instant.now()).toMillis());
        assertThat(writeExceptions.get()).isEqualTo(0);
        assertThat(misses.get()).isEqualTo(0);
        System.out.println(String.format("setup duration %s ms, test duration %s ms", setupDuration, testDuration));
    }
}
