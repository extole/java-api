package com.extole.common.memcached;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.extole.common.memcached.ExtoleMemcachedClient.ValueWithVersion;

@Disabled("for manual execution")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestMemcachedConfig.class})
public class ExtoleMemcachedClientTest {
    private static final String STORE_NAME = "testStore";
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
    public void basicOperations() throws Exception {
        String key = UUID.randomUUID().toString();

        assertTrue(memcachedClient.get(key).isEmpty());

        assertFalse(memcachedClient.touch(key));

        assertTrue(memcachedClient.add(key, "{\"k\":\"test01\"}"));
        assertThat(memcachedClient.get(key).get()).isEqualTo("{\"k\":\"test01\"}");

        assertFalse(memcachedClient.add(key, "{\"k\":\"test01\"}"));
        assertThat(memcachedClient.get(key).get()).isEqualTo("{\"k\":\"test01\"}");

        memcachedClient.set(key, "{\"k\":\"test02\"}");
        assertThat(memcachedClient.get(key).get()).isEqualTo("{\"k\":\"test02\"}");

        memcachedClient.set(key, "{\"k\":\"test03\"}");
        assertThat(memcachedClient.get(key).get()).isEqualTo("{\"k\":\"test03\"}");

        assertTrue(memcachedClient.touch(key));
        assertThat(memcachedClient.get(key).get()).isEqualTo("{\"k\":\"test03\"}");

        assertTrue(memcachedClient.delete(key));
        assertTrue(memcachedClient.get(key).isEmpty());

        assertFalse(memcachedClient.delete(key));
        assertTrue(memcachedClient.get(key).isEmpty());
    }

    @Test
    public void getBulk() throws Exception {
        String key1 = UUID.randomUUID().toString();
        String key2 = UUID.randomUUID().toString();
        String key3 = UUID.randomUUID().toString();
        String key4 = UUID.randomUUID().toString();
        String key5 = UUID.randomUUID().toString();

        String value1 = "{\"k\":\"test01\"}";
        String value2 = "{\"k\":\"test02\"}";
        String value3 = "{\"k\":\"test03\"}";

        assertTrue(memcachedClient.get(Arrays.asList(key1, key2, key3)).isEmpty());

        assertTrue(memcachedClient.add(key1, value1));
        assertTrue(memcachedClient.add(key2, value2));
        assertTrue(memcachedClient.add(key3, value3));

        assertThat(memcachedClient.get(Arrays.asList(key1, key2, key3)))
            .isEqualTo(Map.of(key1, value1, key2, value2, key3, value3));

        assertThat(memcachedClient.get(Arrays.asList(key1, key4, key5))).isEqualTo(Map.of(key1, value1));
    }

    @Test
    public void getWithVersion() throws Exception {
        String key = UUID.randomUUID().toString();

        assertTrue(memcachedClient.getWithVersion(key).isEmpty());

        assertTrue(memcachedClient.add(key, "{\"k\":\"test01\"}"));
        ValueWithVersion<String> valueWithVersion1 = memcachedClient.getWithVersion(key).get();
        assertThat(valueWithVersion1.getValue()).isEqualTo("{\"k\":\"test01\"}");

        memcachedClient.set(key, "{\"k\":\"test02\"}");
        ValueWithVersion<String> valueWithVersion2 = memcachedClient.getWithVersion(key).get();
        assertThat(valueWithVersion2.getValue()).isEqualTo("{\"k\":\"test02\"}");
        assertTrue(valueWithVersion2.getCasVersion() > valueWithVersion1.getCasVersion());

        memcachedClient.set(key, "{\"k\":\"test03\"}");
        ValueWithVersion<String> valueWithVersion3 = memcachedClient.getWithVersion(key).get();
        assertThat(valueWithVersion3.getValue()).isEqualTo("{\"k\":\"test03\"}");
        assertTrue(valueWithVersion3.getCasVersion() > valueWithVersion2.getCasVersion());

        assertTrue(memcachedClient.delete(key));
        assertTrue(memcachedClient.getWithVersion(key).isEmpty());
    }

    @Test
    public void getAndOptionallySetEmpty() throws Exception {
        String key = UUID.randomUUID().toString();
        Optional<String> result = memcachedClient.getAndOptionallySet(key, value -> Optional.empty());
        assertTrue(result.isEmpty());
    }

    @Test
    public void getAndOptionallySetWithoutPreviousValue() throws Exception {
        String key = UUID.randomUUID().toString();
        Optional<String> result = memcachedClient.getAndOptionallySet(key, value -> Optional.of("{\"k\":\"test01\"}"));
        assertThat(result.get()).isEqualTo("{\"k\":\"test01\"}");
    }

    @Test
    public void getAndOptionallySetWithPreviousValue() throws Exception {
        String key = UUID.randomUUID().toString();
        memcachedClient.set(key, "{\"k\":\"test01\"}");
        Optional<String> result = memcachedClient.getAndOptionallySet(key, value -> Optional.empty());
        assertThat(result.get()).isEqualTo("{\"k\":\"test01\"}");
    }

    @Test
    public void getAndOptionallySetWithPreviousValueReplace() throws Exception {
        String key = UUID.randomUUID().toString();
        memcachedClient.set(key, "{\"k\":\"test01\"}");
        Optional<String> result = memcachedClient.getAndOptionallySet(key, value -> Optional.of("{\"k\":\"test02\"}"));
        assertThat(result.get()).isEqualTo("{\"k\":\"test02\"}");
    }

    @Test
    public void getAndOptionallySetFailure() throws Exception {
        String key = UUID.randomUUID().toString();
        try {
            memcachedClient.getAndOptionallySet(key, currentValue -> {
                try {
                    memcachedClient.set(key, "{\"k\":\"test01\"}");
                } catch (ExtoleMemcachedException | InterruptedException e) {
                    e.printStackTrace();
                }
                return Optional.of("{\"k\":\"test02\"}");
            });
            fail("Expecting ExtoleMemcachedException");
        } catch (ExtoleMemcachedException e) {
            assertTrue(e.getMessage().startsWith("Error getAndOptionallySet value for key"));
            assertNull(e.getCause());
        }
    }

    @Test
    public void setWithCasVersion() throws Exception {
        String key = UUID.randomUUID().toString();
        memcachedClient.set(key, "{\"k\":\"test01\"}");
        Optional<ValueWithVersion<String>> valueWithVersion = memcachedClient.getWithVersion(key);
        assertThat(valueWithVersion.get().getValue()).isEqualTo("{\"k\":\"test01\"}");
        memcachedClient.setWithCasVersion(key, "{\"k\":\"test02\"}", valueWithVersion.get());
        assertThat(memcachedClient.getWithVersion(key).get().getValue()).isEqualTo("{\"k\":\"test02\"}");
    }

    @Test
    public void setWithCasVersionDeletedValue() throws Exception {
        String key = UUID.randomUUID().toString();
        memcachedClient.set(key, "{\"k\":\"test01\"}");
        Optional<ValueWithVersion<String>> valueWithVersion = memcachedClient.getWithVersion(key);
        assertThat(valueWithVersion.get().getValue()).isEqualTo("{\"k\":\"test01\"}");

        assertTrue(memcachedClient.delete(key));
        assertTrue(memcachedClient.getWithVersion(key).isEmpty());

        memcachedClient.setWithCasVersion(key, "{\"k\":\"test02\"}", valueWithVersion.get());
        assertThat(memcachedClient.getWithVersion(key).get().getValue()).isEqualTo("{\"k\":\"test02\"}");
    }

    @Test
    public void setWithCasVersionFailOutdatedCas() throws Exception {
        String key = UUID.randomUUID().toString();
        memcachedClient.set(key, "{\"k\":\"test01\"}");
        Optional<ValueWithVersion<String>> valueWithVersion = memcachedClient.getWithVersion(key);
        assertThat(valueWithVersion.get().getValue()).isEqualTo("{\"k\":\"test01\"}");

        memcachedClient.set(key, "{\"k\":\"test02\"}");
        Optional<ValueWithVersion<String>> valueWithVersion2 = memcachedClient.getWithVersion(key);
        assertThat(valueWithVersion2.get().getValue()).isEqualTo("{\"k\":\"test02\"}");
        assertTrue(valueWithVersion2.get().getCasVersion() > valueWithVersion.get().getCasVersion());

        try {
            memcachedClient.setWithCasVersion(key, "{\"k\":\"test02\"}", valueWithVersion.get());
            fail("Expecting OutdatedCasVersionException");
        } catch (OutdatedCasVersionException e) {
            assertTrue(e.getMessage().contains(String.valueOf(valueWithVersion.get().getCasVersion())));
            assertTrue(e.getMessage().contains(String.valueOf(valueWithVersion2.get().getCasVersion())));
        }
    }

}
