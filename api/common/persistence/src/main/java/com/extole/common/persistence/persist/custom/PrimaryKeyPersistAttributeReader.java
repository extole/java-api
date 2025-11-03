package com.extole.common.persistence.persist.custom;

import java.lang.reflect.Method;
import java.util.Objects;

import net.sf.persist.mapping.PersistAttributeReader;

import com.extole.id.PrimaryKey;

public class PrimaryKeyPersistAttributeReader implements PersistAttributeReader {
    @Override
    public Object getValue(Object instance, Method getter) throws Exception {
        PrimaryKey value = (PrimaryKey) getter.invoke(instance);

        return Objects.nonNull(value) ? value.getValue() : null;
    }
}
