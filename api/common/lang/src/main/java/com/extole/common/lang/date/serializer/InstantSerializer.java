package com.extole.common.lang.date.serializer;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public final class InstantSerializer extends StdSerializer<Instant> {

    public static final InstantSerializer INSTANCE = new InstantSerializer();

    private static final com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer BASE_SERIALIZER =
        com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer.INSTANCE;

    private InstantSerializer() {
        super(Instant.class);
    }

    @Override
    public void serialize(Instant value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        BASE_SERIALIZER.serialize(value.truncatedTo(ChronoUnit.MILLIS), generator, provider);
    }
}
