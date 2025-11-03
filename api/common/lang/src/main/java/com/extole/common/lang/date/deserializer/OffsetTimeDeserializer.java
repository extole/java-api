package com.extole.common.lang.date.deserializer;

import java.io.IOException;
import java.time.OffsetTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import com.extole.common.lang.deserializer.CacheableStdDeserializer;

public final class OffsetTimeDeserializer extends CacheableStdDeserializer<OffsetTime> {

    public static final OffsetTimeDeserializer INSTANCE = new OffsetTimeDeserializer();

    private static final com.fasterxml.jackson.datatype.jsr310.deser.OffsetTimeDeserializer BASE_DESERIALIZER =
        com.fasterxml.jackson.datatype.jsr310.deser.OffsetTimeDeserializer.INSTANCE;

    public OffsetTimeDeserializer() {
        super(OffsetTime.class);
    }

    @Override
    public OffsetTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Optional.ofNullable(BASE_DESERIALIZER.deserialize(parser, context))
            .map(result -> result.truncatedTo(ChronoUnit.MILLIS)).orElse(null);
    }
}
