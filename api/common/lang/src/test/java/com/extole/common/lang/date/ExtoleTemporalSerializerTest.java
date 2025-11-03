package com.extole.common.lang.date;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExtoleTemporalSerializerTest {

    private ObjectMapper extoleObjectMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        extoleObjectMapper = new ObjectMapper()
            .registerModule(new ExtoleTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

        objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
    }

    @Test
    public void testExtoleZonedDateTimeSerializer() throws Exception {
        testExtoleTemporalSerializer(ZonedDateTime.class, "\"2021-10-29T12:59:29.845+03:00[Europe/Chisinau]\"",
            "\"2021-10-29T12:59:29.845902+03:00[Europe/Chisinau]\"");
    }

    @Test
    public void testExtoleZonedDateTimeSerializerWithoutZoneId() throws Exception {
        extoleObjectMapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);
        testExtoleTemporalSerializer(ZonedDateTime.class, "\"2021-10-29T12:59:29.845+03:00\"",
            "\"2021-10-29T12:59:29.845902+03:00\"");
    }

    @Test
    public void testExtoleOffsetDateTimeSerializer() throws Exception {
        testExtoleTemporalSerializer(OffsetDateTime.class, "\"2021-10-29T12:59:29.845+03:00\"",
            "\"2021-10-29T12:59:29.845902+03:00\"");
    }

    @Test
    public void testExtoleOffsetTimeSerializer() throws Exception {
        testExtoleTemporalSerializer(OffsetTime.class, "\"15:54:52.658+03:00\"", "\"15:54:52.658123+03:00\"");
    }

    @Test
    public void testExtoleLocalDateTimeSerializer() throws Exception {
        testExtoleTemporalSerializer(LocalDateTime.class, "\"2021-10-29T15:55:00.267\"",
            "\"2021-10-29T15:55:00.26753\"");
    }

    @Test
    public void testExtoleLocalTimeSerializer() throws Exception {
        testExtoleTemporalSerializer(LocalTime.class, "\"15:55:00.268\"", "\"15:55:00.268967\"");
    }

    @Test
    public void testExtoleInstantSerializer() throws Exception {
        testExtoleTemporalSerializer(Instant.class, "\"2021-10-29T13:10:38.507Z\"",
            "\"2021-10-29T13:10:38.507123Z\"");
    }

    @Test
    public void testExtoleTemporalSerializerWith0Nanos() throws Exception {
        testExtoleTemporalSerializer(Instant.class, "\"2021-10-29T13:10:38Z\"",
            "\"2021-10-29T13:10:38Z\"");
    }

    private <T extends Temporal> void testExtoleTemporalSerializer(Class<T> temporalType, String expectedTemporalString,
        String temporalStringWithNanos) throws Exception {
        T temporalWithNanos = objectMapper.readValue(temporalStringWithNanos, temporalType);
        double nanos = temporalWithNanos.getLong(ChronoField.NANO_OF_SECOND);
        double millis = temporalWithNanos.getLong(ChronoField.MILLI_OF_SECOND);
        if (nanos == 0d) {
            assertThat(Double.valueOf(nanos)).isEqualTo(Double.valueOf(millis));
        } else {
            assertThat(Double.valueOf(nanos / millis)).isNotEqualTo(Double.valueOf(1000000));
        }

        String actualTemporalString = extoleObjectMapper.writeValueAsString(temporalWithNanos);
        assertThat(expectedTemporalString).isEqualTo(actualTemporalString);
    }
}
