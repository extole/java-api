package com.extole.common.lang.date.deserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;

import com.extole.common.lang.deserializer.CacheableStdDeserializer;

public final class OffsetDateTimeDeserializer extends CacheableStdDeserializer<OffsetDateTime> {

    public static final OffsetDateTimeDeserializer INSTANCE = new OffsetDateTimeDeserializer();

    private static final InstantDeserializer<OffsetDateTime> BASE_DESERIALIZER = InstantDeserializer.OFFSET_DATE_TIME;

    private OffsetDateTimeDeserializer() {
        super(OffsetDateTime.class);
    }

    @Override
    public OffsetDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Optional.ofNullable(BASE_DESERIALIZER.deserialize(parser, context))
            .map(result -> result.truncatedTo(ChronoUnit.MILLIS)).orElse(null);
    }
}
