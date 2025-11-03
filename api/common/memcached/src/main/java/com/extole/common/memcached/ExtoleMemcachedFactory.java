package com.extole.common.memcached;

public interface ExtoleMemcachedFactory {

    ExtoleMemcachedClientBuilder create();

    ExtoleMemcachedClientBuilder create(String clientLibrary);
}
