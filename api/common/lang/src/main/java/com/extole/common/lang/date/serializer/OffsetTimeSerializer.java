package com.extole.common.lang.date.serializer;

import java.io.IOException;
import java.time.OffsetTime;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public final class OffsetTimeSerializer extends StdSerializer<OffsetTime> {

    public static final OffsetTimeSerializer INSTANCE = new OffsetTimeSerializer();

    private static final com.fasterxml.jackson.datatype.jsr310.ser.OffsetTimeSerializer BASE_SERIALIZER =
        com.fasterxml.jackson.datatype.jsr310.ser.OffsetTimeSerializer.INSTANCE;

    private OffsetTimeSerializer() {
        super(OffsetTime.class);
    }

    @Override
    public void serialize(OffsetTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        BASE_SERIALIZER.serialize(value.truncatedTo(ChronoUnit.MILLIS), generator, provider);
    }
}
