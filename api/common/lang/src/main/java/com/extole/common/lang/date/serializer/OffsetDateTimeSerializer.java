package com.extole.common.lang.date.serializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public final class OffsetDateTimeSerializer extends StdSerializer<OffsetDateTime> {

    public static final OffsetDateTimeSerializer INSTANCE = new OffsetDateTimeSerializer();

    private static final com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer BASE_SERIALIZER =
        com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer.INSTANCE;

    private OffsetDateTimeSerializer() {
        super(OffsetDateTime.class);
    }

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator generator, SerializerProvider provider)
        throws IOException {
        BASE_SERIALIZER.serialize(value.truncatedTo(ChronoUnit.MILLIS), generator, provider);
    }
}
