package com.extole.common.memcached;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MemcachedKey {
    private static final int MEMCACHED_MAXIMUM_KEY_LENGTH = 250;
    private final String keyValue;

    public MemcachedKey(String key) throws MalformedMemcachedKeyException {
        try {
            this.keyValue = URLEncoder.encode(key, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // should not happen
            throw new MalformedMemcachedKeyException("Unable to encode memcached key: " + key, e);
        }
        if (keyValue.length() > MEMCACHED_MAXIMUM_KEY_LENGTH) {
            throw new MalformedMemcachedKeyException("Memcached maximum length exceeded by key: " + key);
        }
    }

    public String getValue() {
        return keyValue;
    }

    @Override
    public String toString() {
        return keyValue;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(keyValue);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null || otherObject.getClass() != getClass()) {
            return false;
        }

        MemcachedKey other = (MemcachedKey) otherObject;
        return Objects.equals(keyValue, other.keyValue);
    }

}
