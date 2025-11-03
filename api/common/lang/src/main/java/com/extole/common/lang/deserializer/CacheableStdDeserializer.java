package com.extole.common.lang.deserializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public abstract class CacheableStdDeserializer<T> extends StdDeserializer<T> {

    protected CacheableStdDeserializer(Class<?> clazz) {
        super(clazz);
    }

    protected CacheableStdDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected CacheableStdDeserializer(StdDeserializer<?> deserializer) {
        super(deserializer);
    }

    @Override
    public final boolean isCachable() {
        return true;
    }

}
