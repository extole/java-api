package com.extole.common.persistence.persist.custom;

import java.lang.reflect.Method;
import java.util.Objects;

import net.sf.persist.mapping.PersistAttributeWriter;

import com.extole.id.PrimaryKey;

public class PrimaryKeyPersistAttributeWriter implements PersistAttributeWriter {
    @Override
    public void setValue(Object instance, Method setter, Object value) throws Exception {
        PrimaryKey<Object> valueToSet = Objects.nonNull(value) ? PrimaryKey.valueOf((Long) value) : null;

        setter.invoke(instance, valueToSet);
    }

    @Override
    public Class getJDBCDataType(Method setter) {
        return Long.class;
    }
}
