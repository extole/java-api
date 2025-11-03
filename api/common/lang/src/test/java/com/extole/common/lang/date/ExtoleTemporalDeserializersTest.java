package com.extole.common.lang.date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

public class ExtoleTemporalDeserializersTest {

    private static final ZoneId ZONE = ZoneId.of("Europe/Chisinau");
    private static final ObjectMapper EXTOLE_OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new ExtoleTimeModule(() -> Optional.of(ZONE)))
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
        .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
        .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

    @Test
    public void testExtoleTemporalDeserializationFailsWhenInvalidStringIsProvided() {
        String invalidDate = "\"2015-INVALID\"";
        List<Class<? extends Temporal>> temporalTypes =
            Lists.newArrayList(Instant.class, OffsetTime.class, OffsetDateTime.class, LocalTime.class,
                LocalDateTime.class);

        for (Class<? extends Temporal> temporalType : temporalTypes) {
            assertThrows(InvalidFormatException.class,
                () -> EXTOLE_OBJECT_MAPPER.readValue(invalidDate, temporalType));
        }
    }

    @Test
    public void testExtoleZonedDateTimeDeserializationFailsWhenInvalidStringIsProvided() {
        String invalidDate = "\"2015:10:10\"";
        assertThrows(ZonedDateTimeDeserializationException.class,
            () -> EXTOLE_OBJECT_MAPPER.readValue(invalidDate, ZonedDateTime.class));
    }

    @Test
    public void testExtoleZonedDateTimeDeserializerFromNotCompleteZonedDateTimeStrings() throws Exception {
        String date1 = "\"2021-10-10\"";
        String date2 = "\"2021-10-10Z\"";
        String date3 = "\"2021-10-10T10:10\"";
        String date4 = "\"2021-10-10T10:10Z\"";
        String date5 = "\"2021-10-10T10:10:10\"";
        String date6 = "\"2021-10-10T10:10:10Z\"";
        String date7 = "\"2021-10-10T10:10:10.11\"";
        String date8 = "\"2021-10-10T10:10:10.11Z\"";
        String date9 = "\"2021-10-10T10:10:10.111111\"";
        String date10 = "\"2021-10-10T10:10:10.111111Z\"";
        List<String> dates = Arrays.asList(date1, date2, date3, date4, date5, date6, date7, date8, date9, date10);
        List<ZonedDateTime> expectedDates = dates.stream()
            .map(dateString -> dateString.replace("\"", ""))
            .map(dateString -> {
                try {
                    return new DateTimeBuilder().withDateString(dateString).withDefaultTimezone(ZONE).build()
                        .truncatedTo(ChronoUnit.MILLIS);
                } catch (DateTimeBuilderValidationException e) {
                    throw new RuntimeException("Could not parse date string: ", e);
                }
            }).collect(Collectors.toList());

        for (int i = 0; i < dates.size(); i++) {
            testExtoleTemporalWithOffsetDeserializerFromNotCompleteDateTimeString(dates.get(i), ZonedDateTime.class,
                expectedDates.get(i));
        }
    }

    private <T extends Temporal> void testExtoleTemporalWithOffsetDeserializerFromNotCompleteDateTimeString(String date,
        Class<T> temporalType, T expectedTemporal)
        throws Exception {
        T actualTemporal = EXTOLE_OBJECT_MAPPER.readValue(date, temporalType);
        assertThat(actualTemporal).isEqualTo(expectedTemporal);

        double millis = actualTemporal.getLong(ChronoField.MILLI_OF_SECOND);
        double nanos = actualTemporal.getLong(ChronoField.NANO_OF_SECOND);
        if (millis == 0d) {
            assertThat(Double.valueOf(nanos)).isEqualTo(Double.valueOf(millis));
        } else {
            assertThat(Double.valueOf(nanos / millis)).isEqualTo(Double.valueOf(1000000));
        }
    }

    @Test
    public void testExtoleZonedDateTimeDeserializer() throws Exception {
        testExtoleTemporalDeserializer(ZonedDateTime.now());
    }

    @Test
    public void testExtoleOffsetTimeDeserializer() throws Exception {
        testExtoleTemporalDeserializer(OffsetTime.now());
    }

    @Test
    public void testExtoleOffsetDateTimeDeserializer() throws Exception {
        testExtoleTemporalDeserializer(OffsetDateTime.now());
    }

    @Test
    public void testExtoleLocalTimeDeserializer() throws Exception {
        testExtoleTemporalDeserializer(LocalTime.now());
    }

    @Test
    public void testExtoleLocalDateTimeDeserializer() throws Exception {
        testExtoleTemporalDeserializer(LocalDateTime.now());
    }

    @Test
    public void testExtoleInstantDeserializer() throws Exception {
        testExtoleTemporalDeserializer(Instant.now());
    }

    @Test
    public void testTemporalWith0Millis() throws Exception {
        // 123 in the last field means 123 nanoseconds, or 0.000123 millis
        testExtoleTemporalDeserializer(LocalDateTime.of(10, 10, 10, 10, 10, 11, 123));
    }

    private void testExtoleTemporalDeserializer(Temporal now) throws Exception {
        String temporalString = EXTOLE_OBJECT_MAPPER.writeValueAsString(now);
        String temporalWithNanosString = OBJECT_MAPPER.writeValueAsString(now);

        Temporal temporal = EXTOLE_OBJECT_MAPPER.readValue(temporalString, now.getClass());
        Temporal temporal2 = EXTOLE_OBJECT_MAPPER.readValue(temporalWithNanosString, now.getClass());

        double nanos = temporal.getLong(ChronoField.NANO_OF_SECOND);
        double millis = temporal.getLong(ChronoField.MILLI_OF_SECOND);
        if (millis == 0d) {
            assertThat(Double.valueOf(nanos)).isEqualTo(Double.valueOf(millis));
        } else {
            assertThat(Double.valueOf(nanos / millis)).isEqualTo(Double.valueOf(1000000));
        }
        assertThat(temporal2).isEqualTo(temporal);
    }
}
