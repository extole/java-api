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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

public class ExtoleTemporalKeyDeserializersTest {

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
    public void testExtoleTemporalKeyDeserializerFailsWhenInvalidStringIsProvided() {
        String invalidDatesMapString = "{\"2021-INVALID\":\"nothing\"}";
        List<Class<? extends Temporal>> temporalTypes =
            Lists.newArrayList(Instant.class, OffsetTime.class, OffsetDateTime.class, LocalTime.class,
                LocalDateTime.class);
        for (Class<? extends Temporal> temporalType : temporalTypes) {
            JavaType parameterizedTemporalMapType =
                EXTOLE_OBJECT_MAPPER.getTypeFactory().constructParametricType(Map.class, temporalType, String.class);
            assertThrows(InvalidFormatException.class,
                () -> EXTOLE_OBJECT_MAPPER.readValue(invalidDatesMapString, parameterizedTemporalMapType));
        }
    }

    @Test
    public void testExtoleZonedDateTimeKeyDeserializerFailsWhenInvalidStringIsProvided() {
        String invalidDatesMapString = "{\"2021:11:11\":\"nothing\"}";
        JavaType parameterizedTemporalMapType =
            EXTOLE_OBJECT_MAPPER.getTypeFactory().constructParametricType(Map.class, ZonedDateTime.class, String.class);
        assertThrows(ZonedDateTimeDeserializationException.class,
            () -> EXTOLE_OBJECT_MAPPER.readValue(invalidDatesMapString, parameterizedTemporalMapType));
    }

    @Test
    public void testExtoleZonedDateTimeKeyDeserilizerWithNotCompleteZonedDateTimeString() throws Exception {
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

        List<String> dateMapStrings = dates.stream().map(dateString -> String.format("{%s:\"nothing\"}", dateString))
            .collect(Collectors.toList());
        List<ZonedDateTime> expectedDates = dates.stream()
            .map(dateString -> dateString.replace("\"", ""))
            .map(dateString -> {
                try {
                    return new DateTimeBuilder().withDateString(dateString).withDefaultTimezone(ZONE).build()
                        .truncatedTo(ChronoUnit.MILLIS);
                } catch (DateTimeBuilderValidationException e) {
                    throw new RuntimeException("Could not parse date string", e);
                }
            }).collect(Collectors.toList());

        for (int i = 0; i < dateMapStrings.size(); i++) {
            testExtoleTemporalWithOffsetKeyDeserializerFromNotCompleteDateTimeString(dateMapStrings.get(i),
                ZonedDateTime.class, expectedDates.get(i));
        }
    }

    public <T extends Temporal> void testExtoleTemporalWithOffsetKeyDeserializerFromNotCompleteDateTimeString(
        String datesMapString, Class<T> temporalType, T expectedTemporal) throws Exception {
        JavaType parameterizedTemporalMapType =
            EXTOLE_OBJECT_MAPPER.getTypeFactory().constructParametricType(Map.class, temporalType, String.class);
        Map<T, String> datesMap = EXTOLE_OBJECT_MAPPER.readValue(datesMapString, parameterizedTemporalMapType);

        T actualTemporal = datesMap.keySet().stream().findFirst().get();
        assertThat(actualTemporal).isEqualTo(expectedTemporal);

        long millis = actualTemporal.getLong(ChronoField.MILLI_OF_SECOND);
        long nanos = actualTemporal.getLong(ChronoField.NANO_OF_SECOND);
        if (millis == 0L) {
            assertThat(nanos).isEqualTo(millis);
        } else {
            assertThat((nanos / millis)).isEqualTo(1000000L);
        }
    }

    @Test
    public void testExtoleZonedDateTimeKeyDeserializer() throws Exception {
        testExtoleKeyDeserializer(ImmutableMap.of(ZonedDateTime.now(), "nothing"));
    }

    @Test
    public void testExtoleOffsetDateTimeKeyDeserializer() throws Exception {
        testExtoleKeyDeserializer(ImmutableMap.of(OffsetDateTime.now(), "nothing"));
    }

    @Test
    public void testExtoleOffsetTimeKeyDeserializer() throws Exception {
        testExtoleKeyDeserializer(ImmutableMap.of(OffsetTime.now(), "nothing"));
    }

    @Test
    public void testExtoleLocalDateTimeKeyDeserializer() throws Exception {
        testExtoleKeyDeserializer(ImmutableMap.of(LocalDateTime.now(), "nothing"));
    }

    @Test
    public void testExtoleLocalTimeKeyDeserializer() throws Exception {
        testExtoleKeyDeserializer(ImmutableMap.of(LocalTime.now(), "nothing"));
    }

    @Test
    public void testExtoleInstantKeyDeserializer() throws Exception {
        testExtoleKeyDeserializer(ImmutableMap.of(Instant.now(), "nothing"));
    }

    @Test
    public void testTemporalWith0Millis() throws Exception {
        // 123 in the last field means 123 nanoseconds, or 0.000123 millis
        testExtoleKeyDeserializer(ImmutableMap.of(LocalDateTime.of(10, 10, 10, 10, 10, 11, 123), "nothing"));
    }

    @SuppressWarnings("unchecked")
    public <T extends Temporal> void testExtoleKeyDeserializer(Map<T, String> map)
        throws Exception {
        String temporalMapString = EXTOLE_OBJECT_MAPPER.writeValueAsString(map);
        String temporalMapWithNanosString = OBJECT_MAPPER.writeValueAsString(map);

        Class<T> temporalType = (Class<T>) map.keySet().stream().findFirst().get().getClass();
        JavaType parameterizedTemporalMapType =
            EXTOLE_OBJECT_MAPPER.getTypeFactory().constructParametricType(Map.class, temporalType, String.class);

        Map<T, String> temporalMap = EXTOLE_OBJECT_MAPPER.readValue(temporalMapString, parameterizedTemporalMapType);
        Map<T, String> temporalMap2 =
            EXTOLE_OBJECT_MAPPER.readValue(temporalMapWithNanosString, parameterizedTemporalMapType);

        Temporal temporal = temporalMap.keySet().stream().findFirst().get();
        Temporal temporal2 = temporalMap2.keySet().stream().findFirst().get();

        double nanos = temporal.getLong(ChronoField.NANO_OF_SECOND);
        double millis = temporal.getLong(ChronoField.MILLI_OF_SECOND);
        if (millis == 0L) {
            assertThat(Double.valueOf(nanos)).isEqualTo(Double.valueOf(millis));
        } else {
            assertThat(Double.valueOf(nanos / millis)).isEqualTo(Double.valueOf(1000000));
        }
        assertThat(temporal2).isEqualTo(temporal);
    }
}
