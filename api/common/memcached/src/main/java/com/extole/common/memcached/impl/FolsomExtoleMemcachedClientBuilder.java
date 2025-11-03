package com.extole.common.memcached.impl;

import com.extole.common.memcached.ExtoleMemcachedClientBuilder;

public interface FolsomExtoleMemcachedClientBuilder extends ExtoleMemcachedClientBuilder {
    String CLIENT_LIBRARY_NAME = "folsom";

    FolsomExtoleMemcachedClientBuilder withMaxOutstandingRequests(int maxOutstandingRequests);
}
