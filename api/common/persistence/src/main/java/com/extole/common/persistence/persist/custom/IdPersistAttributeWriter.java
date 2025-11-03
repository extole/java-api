package com.extole.common.persistence.persist.custom;

import java.lang.reflect.Method;
import java.util.Objects;

import net.sf.persist.mapping.PersistAttributeWriter;

import com.extole.id.Id;

public class IdPersistAttributeWriter implements PersistAttributeWriter {
    @Override
    public void setValue(Object instance, Method setter, Object value) throws Exception {
        Id<Object> valueToSet = Objects.nonNull(value) ? Id.valueOf(String.valueOf(value)) : null;

        setter.invoke(instance, valueToSet);
    }

    @Override
    public Class getJDBCDataType(Method setter) {
        return String.class;
    }
}
