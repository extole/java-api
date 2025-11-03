package com.extole.common.lang.date.deserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import com.extole.common.lang.deserializer.CacheableStdDeserializer;

public final class InstantDeserializer extends CacheableStdDeserializer<Instant> {

    public static final InstantDeserializer INSTANCE = new InstantDeserializer();

    private static final com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer<Instant> BASE_DESERIALIZER =
        com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer.INSTANT;

    private InstantDeserializer() {
        super(Instant.class);
    }

    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Optional.ofNullable(BASE_DESERIALIZER.deserialize(parser, context))
            .map(result -> result.truncatedTo(ChronoUnit.MILLIS)).orElse(null);
    }
}
