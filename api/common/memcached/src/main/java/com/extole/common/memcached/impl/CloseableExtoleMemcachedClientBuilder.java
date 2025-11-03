package com.extole.common.memcached.impl;

import com.extole.common.memcached.ExtoleMemcachedClientBuilder;

interface CloseableExtoleMemcachedClientBuilder extends ExtoleMemcachedClientBuilder {
    void close();
}
