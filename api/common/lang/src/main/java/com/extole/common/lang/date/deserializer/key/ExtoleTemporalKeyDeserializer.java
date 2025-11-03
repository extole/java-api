package com.extole.common.lang.date.deserializer.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;

import com.extole.common.lang.date.ExtoleDateTimeFormatters;

public class ExtoleTemporalKeyDeserializer<T extends Temporal> extends KeyDeserializer {

    public static final ExtoleTemporalKeyDeserializer<Instant> INSTANT_KEY_DESERIALIZER =
        new ExtoleTemporalKeyDeserializer<>(
            (key, context) -> {
                try {
                    return ExtoleDateTimeFormatters.ISO_INSTANT.parse(key, Instant::from)
                        .truncatedTo(ChronoUnit.MILLIS);
                } catch (DateTimeException e) {
                    return handleDateTimeException(context, Instant.class, e, key);
                }
            });

    public static final ExtoleTemporalKeyDeserializer<LocalDateTime> LOCAL_DATE_TIME_KEY_DESERIALIZER =
        new ExtoleTemporalKeyDeserializer<>(
            (key, context) -> {
                try {
                    return LocalDateTime.parse(key, ExtoleDateTimeFormatters.ISO_LOCAL_DATE_TIME);
                } catch (DateTimeException e) {
                    return handleDateTimeException(context, LocalDateTime.class, e, key);
                }
            });

    public static final ExtoleTemporalKeyDeserializer<LocalTime> LOCAL_TIME_KEY_DESERIALIZER =
        new ExtoleTemporalKeyDeserializer<>(
            (key, context) -> {
                try {
                    return LocalTime.parse(key, ExtoleDateTimeFormatters.ISO_LOCAL_TIME);
                } catch (DateTimeException e) {
                    return handleDateTimeException(context, LocalTime.class, e, key);
                }
            });

    public static final ExtoleTemporalKeyDeserializer<OffsetTime> OFFSET_TIME_KEY_DESERIALIZER =
        new ExtoleTemporalKeyDeserializer<>(
            (key, context) -> {
                try {
                    return OffsetTime.parse(key, ExtoleDateTimeFormatters.ISO_OFFSET_TIME);
                } catch (DateTimeException e) {
                    return handleDateTimeException(context, OffsetTime.class, e, key);
                }
            });

    public static final ExtoleTemporalKeyDeserializer<OffsetTime> OFFSET_DATE_TIME_KEY_DESERIALIZER =
        new ExtoleTemporalKeyDeserializer<>(
            (key, context) -> {
                try {
                    return OffsetTime.parse(key, ExtoleDateTimeFormatters.ISO_OFFSET_DATE_TIME);
                } catch (DateTimeException e) {
                    return handleDateTimeException(context, OffsetTime.class, e, key);
                }
            });

    private final TemporalKeyDeserializerThrowingBiFunction<T, IOException> deserializerFunction;

    ExtoleTemporalKeyDeserializer(TemporalKeyDeserializerThrowingBiFunction<T, IOException> deserializerFunction) {
        this.deserializerFunction = deserializerFunction;
    }

    @Override
    public T deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return deserializerFunction.apply(key, ctxt);
    }

    @SuppressWarnings("unchecked")
    static <T extends Temporal> T handleDateTimeException(DeserializationContext context, Class<?> type,
        DateTimeException dateTimeException, String value) throws IOException {
        try {
            return (T) context.handleWeirdKey(type, value, "Failed to deserialize %s: (%s) %s", type.getName(),
                dateTimeException.getClass().getName(), dateTimeException.getMessage());
        } catch (JsonMappingException e) {
            e.initCause(dateTimeException);
            throw e;
        } catch (IOException e) {
            if (null == e.getCause()) {
                e.initCause(dateTimeException);
            }
            throw JsonMappingException.fromUnexpectedIOE(e);
        }
    }
}
