package com.extole.common.memcached.impl;

import com.extole.common.lang.ToString;
import com.extole.common.memcached.ExtoleMemcachedClient.ValueWithVersion;

class ValueWithVersionImpl<V> implements ValueWithVersion<V> {
    private final V value;
    private final long cas;
    private final String clientName;

    ValueWithVersionImpl(V value, long cas, String clientName) {
        this.value = value;
        this.cas = cas;
        this.clientName = clientName;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public long getCasVersion() {
        return cas;
    }

    @Override
    public String getClientName() {
        return clientName;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
