package com.extole.common.lang.date.deserializer.key;

import java.io.IOException;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.databind.DeserializationContext;

@FunctionalInterface
interface TemporalKeyDeserializerThrowingBiFunction<T extends Temporal, E extends IOException> {
    T apply(String temporalKey, DeserializationContext deserializationContext) throws E;
}
