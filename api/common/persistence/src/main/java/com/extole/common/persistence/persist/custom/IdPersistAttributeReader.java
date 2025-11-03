package com.extole.common.persistence.persist.custom;

import java.lang.reflect.Method;
import java.util.Objects;

import net.sf.persist.mapping.PersistAttributeReader;

import com.extole.id.Id;

public class IdPersistAttributeReader implements PersistAttributeReader {
    @Override
    public Object getValue(Object instance, Method getter) throws Exception {
        Id value = (Id) getter.invoke(instance);

        return Objects.nonNull(value) ? value.getValue() : null;
    }
}
