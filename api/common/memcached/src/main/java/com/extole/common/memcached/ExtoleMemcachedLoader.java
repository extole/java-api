package com.extole.common.memcached;

@FunctionalInterface
public interface ExtoleMemcachedLoader<K, V> {

    V load(K key);
}
