package com.extole.common.hbase;

import com.extole.common.lang.ToString;

public record PageRequest(int offset, int limit) {

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
