package com.extole.common.lang.date;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;

import org.junit.jupiter.api.Test;

public class ExtoleDateTimeFormattersTest {

    private static final ZoneId ZONE = ZoneId.of("Europe/Chisinau");

    @Test
    public void testIsoLocalTimeFormatter() {
        LocalTime now = LocalTime.of(10, 10, 10, 123456);
        LocalTime nowWithMillis = now.truncatedTo(ChronoUnit.MILLIS);
        testTemporalFormatter(now, nowWithMillis, ExtoleDateTimeFormatters.ISO_LOCAL_TIME,
            TemporalQueries.localTime());
    }

    @Test
    public void testIsoLocalDateTimeFormatter() {
        LocalDateTime now = LocalDateTime.of(10, 10, 10, 10, 10, 11, 123456);
        LocalDateTime nowWithMillis = now.truncatedTo(ChronoUnit.MILLIS);
        testTemporalFormatter(now, nowWithMillis, ExtoleDateTimeFormatters.ISO_LOCAL_DATE_TIME,
            TemporalQueries.localTime());
    }

    @Test
    public void testIsoOffsetTimeFormatter() {
        OffsetTime now = OffsetTime.of(10, 10, 12, 123456, ZONE.getRules().getOffset(Instant.now()));
        OffsetTime nowWithMillis = now.truncatedTo(ChronoUnit.MILLIS);
        testTemporalFormatter(now, nowWithMillis, ExtoleDateTimeFormatters.ISO_OFFSET_TIME,
            TemporalQueries.localTime());
    }

    @Test
    public void testIsoOffsetDateTimeFormatter() {
        OffsetDateTime now =
            OffsetDateTime.of(10, 10, 10, 10, 10, 13, 123456, ZONE.getRules().getOffset(Instant.now()));
        OffsetDateTime nowWithMillis = now.truncatedTo(ChronoUnit.MILLIS);
        testTemporalFormatter(now, nowWithMillis, ExtoleDateTimeFormatters.ISO_OFFSET_DATE_TIME,
            TemporalQueries.localTime());
    }

    @Test
    public void testIsoZonedDateTimeFormatter() {
        ZonedDateTime now = ZonedDateTime.of(10, 10, 10, 10, 10, 14, 123456, ZONE);
        ZonedDateTime nowWithMillis = now.truncatedTo(ChronoUnit.MILLIS);
        testTemporalFormatter(now, nowWithMillis, ExtoleDateTimeFormatters.ISO_ZONED_DATE_TIME,
            TemporalQueries.localTime());
    }

    private <T extends Temporal> void testTemporalFormatter(T temporalWithNanos, T temporalWithMillis,
        DateTimeFormatter formatter, TemporalQuery<LocalTime> temporalQuery) {
        String actualLocalTimeString = formatter.format(temporalWithNanos);
        String expectedLocalTimeString = temporalWithMillis.toString();
        assertThat(actualLocalTimeString).isEqualTo(expectedLocalTimeString);

        String temporalWithNanosString = temporalWithNanos.toString();
        LocalTime temporalFromString = formatter.parse(temporalWithNanosString, temporalQuery);
        assertThat(temporalFromString.getLong(ChronoField.MILLI_OF_SECOND))
            .isEqualTo(temporalWithMillis.getLong(ChronoField.MILLI_OF_SECOND));
        assertThat(temporalFromString.getLong(ChronoField.NANO_OF_SECOND))
            .isEqualTo(temporalWithMillis.getLong(ChronoField.NANO_OF_SECOND));
    }
}
