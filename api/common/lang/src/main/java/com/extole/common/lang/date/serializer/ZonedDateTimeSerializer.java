package com.extole.common.lang.date.serializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public final class ZonedDateTimeSerializer extends StdSerializer<ZonedDateTime> {

    public static final ZonedDateTimeSerializer INSTANCE = new ZonedDateTimeSerializer();

    private static final com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer BASE_SERIALIZER =
        com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer.INSTANCE;

    private ZonedDateTimeSerializer() {
        super(ZonedDateTime.class);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator generator, SerializerProvider provider)
        throws IOException {
        BASE_SERIALIZER.serialize(value.truncatedTo(ChronoUnit.MILLIS), generator, provider);
    }
}
