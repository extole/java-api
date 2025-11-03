package com.extole.common.persistence.persist;

import com.extole.common.lang.ToString;

public class DatabaseName {

    private final String name;

    public DatabaseName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
