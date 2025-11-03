package com.extole.common.lang.date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

public class DateTimeBuilderTest {

    @Test
    public void testDateTime() throws Exception {
        assertThat(format(new DateTimeBuilder().withDateString("2018").withDefaultTimezone(ZoneOffset.UTC).build()))
            .isEqualTo("2018-01-01T00:00:00.000Z");
        assertThat(format(new DateTimeBuilder().withDateString("2018-07").withDefaultTimezone(ZoneOffset.UTC).build()))
            .isEqualTo("2018-07-01T00:00:00.000Z");
        assertThat(
            format(new DateTimeBuilder().withDateString("2018-07-17").withDefaultTimezone(ZoneOffset.UTC).build()))
                .isEqualTo("2018-07-17T00:00:00.000Z");
        assertThat(
            format(new DateTimeBuilder().withDateString("2018-07-17T22").withDefaultTimezone(ZoneOffset.UTC).build()))
                .isEqualTo("2018-07-17T22:00:00.000Z");
        assertThat(format(
            new DateTimeBuilder().withDateString("2018-07-17T22:15").withDefaultTimezone(ZoneOffset.UTC).build()))
                .isEqualTo("2018-07-17T22:15:00.000Z");
        assertThat(
            format(new DateTimeBuilder().withDateString("2018-07-17T22:15:34").withDefaultTimezone(ZoneOffset.UTC)
                .build())).isEqualTo("2018-07-17T22:15:34.000Z");
        assertThat(
            format(new DateTimeBuilder().withDateString("2018-07-17T22:15:34.159").withDefaultTimezone(ZoneOffset.UTC)
                .build())).isEqualTo("2018-07-17T22:15:34.159Z");
    }

    @Test
    public void testparseStringDateWithOneToEightDigitsPrecision() throws Exception {
        String stringDate = "2018-01-01T00:00:00.%s1Z";
        for (int i = 1; i <= 8; i++) {
            ZonedDateTime zonedDateTime = new DateTimeBuilder()
                .withDateString(String.format(stringDate, Strings.repeat("0", i)))
                .withDefaultTimezone(ZoneOffset.UTC)
                .build();
            double expectedNanos = 10000000 / Math.pow(10, i - 1);
            assertThat((double) zonedDateTime.getNano()).isEqualTo(expectedNanos);
        }
    }

    @Test
    public void testParseStringDateWithMicroseconds() throws Exception {
        String stringDate = "2018-01-01T00:00:00.000123Z";
        ZonedDateTime zonedDateTime = new DateTimeBuilder()
            .withDateString(stringDate)
            .withDefaultTimezone(ZoneOffset.UTC)
            .build();

        assertThat(zonedDateTime.getYear()).isEqualTo(2018);
        assertThat(zonedDateTime.getMonth().getValue()).isEqualTo(1);
        assertThat(zonedDateTime.getDayOfMonth()).isEqualTo(1);
        assertThat(zonedDateTime.getNano()).isEqualTo(123000);
    }

    @Test
    public void testParseStringDateWithNanoseconds() throws Exception {
        String stringDate = "2018-01-01T00:00:00.000123456Z";
        ZonedDateTime zonedDateTime = new DateTimeBuilder()
            .withDateString(stringDate)
            .withDefaultTimezone(ZoneOffset.UTC)
            .build();

        assertThat(zonedDateTime.getYear()).isEqualTo(2018);
        assertThat(zonedDateTime.getMonth().getValue()).isEqualTo(1);
        assertThat(zonedDateTime.getDayOfMonth()).isEqualTo(1);
        assertThat(zonedDateTime.getNano()).isEqualTo(123456);
    }

    @Test
    public void testDateTimeForTimeParsingDisabled() throws Exception {
        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018")
            .withDefaultTimezone(ZoneOffset.UTC).build())).isEqualTo("2018-01-01T00:00:00.000Z");
        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-07")
            .withDefaultTimezone(ZoneOffset.UTC).build())).isEqualTo("2018-07-01T00:00:00.000Z");
        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-07-17")
            .withDefaultTimezone(ZoneOffset.UTC).build())).isEqualTo("2018-07-17T00:00:00.000Z");
        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-07-17T22")
            .withDefaultTimezone(ZoneOffset.UTC).build())).isEqualTo("2018-07-17T00:00:00.000Z");
        assertThat(format(
            new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-07-17T22:15")
                .withDefaultTimezone(ZoneOffset.UTC).build())).isEqualTo("2018-07-17T00:00:00.000Z");
        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-07-17T22:15:34")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build())).isEqualTo("2018-07-17T00:00:00.000Z");
        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-07-17T22:15:34.159")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build())).isEqualTo("2018-07-17T00:00:00.000Z");
    }

    @Test
    public void testInvalidDateTime() {
        assertThrows(DateTimeBuilderValidationException.class,
            () -> new DateTimeBuilder().withDateString("2018-07-17T22:15:34.159/2018-07-17T22:15:34.159")
                .withDefaultTimezone(ZoneOffset.UTC)
                .build());
    }

    @Test
    public void testTimeZone() throws Exception {
        assertThat(format(new DateTimeBuilder().withDateString("2018PST").withDefaultTimezone(ZoneOffset.UTC).build()))
            .isEqualTo("2018-01-01T00:00:00.000PST");
        assertThat(format(new DateTimeBuilder().withDateString("2018-07-17T22:15:34.159+08:00")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build())).isEqualTo("2018-07-17T22:15:34.159+08:00");
        assertThat(format(new DateTimeBuilder().withDateString("2018-11-17T22:15:34.159")
            .withDefaultTimezone(ZoneId.of("PST", ZoneId.SHORT_IDS))
            .build())).isEqualTo("2018-11-17T22:15:34.159PST");
        assertThat(format(new DateTimeBuilder().withDateString("2018-11-17T22:15:34.159")
            .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
            .build())).isEqualTo("2018-11-17T22:15:34.159PST");

        assertThat(format(new DateTimeBuilder().withDateString("2018-11-17T22:15:34+02")
            .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
            .build())).isEqualTo("2018-11-17T22:15:34.000+02:00");

        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-11-17T22:15:34+02")
            .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
            .build())).isEqualTo("2018-11-17T00:00:00.000+02:00");

        assertThat(format(new DateTimeBuilder().withDateString("2018-11-17T22:15:34-0400")
            .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
            .build())).isEqualTo("2018-11-17T22:15:34.000-04:00");

        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-11-17T22:15:34-0400")
            .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
            .build())).isEqualTo("2018-11-17T00:00:00.000-04:00");

        assertThat(format(new DateTimeBuilder().withDateString("2018-11-17T22:15:34+04:00")
            .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
            .build())).isEqualTo("2018-11-17T22:15:34.000+04:00");

        assertThat(
            format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-11-17T22:15:34+04:00")
                .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
                .build())).isEqualTo("2018-11-17T00:00:00.000+04:00");

        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(true).withDateString("2018-11-17T22:15:34+00")
            .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
            .build())).isEqualTo("2018-11-17T22:15:34.000Z");

        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(true).withDateString("2018-11-17T22:15:34+0000")
            .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
            .build())).isEqualTo("2018-11-17T22:15:34.000Z");

        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(true).withDateString("2018-11-17T22:15:34+00:00")
            .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
            .build())).isEqualTo("2018-11-17T22:15:34.000Z");
    }

    @Test
    public void testTimeZoneForTimeParsingDisabled() throws Exception {
        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018PST")
            .withDefaultTimezone(ZoneOffset.UTC).build())).isEqualTo("2018-01-01T00:00:00.000PST");
        assertThat(
            format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-07-17T22:15:34.159+08:00")
                .withDefaultTimezone(ZoneOffset.UTC)
                .build())).isEqualTo("2018-07-17T00:00:00.000+08:00");
        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-11-17T22:15:34.159")
            .withDefaultTimezone(ZoneId.of("PST", ZoneId.SHORT_IDS))
            .build())).isEqualTo("2018-11-17T00:00:00.000PST");
        assertThat(format(new DateTimeBuilder().withTimeParsingEnabled(false).withDateString("2018-11-17T22:15:34.159")
            .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
            .build())).isEqualTo("2018-11-17T00:00:00.000PST");
    }

    @Test
    public void testInvalidDate() {
        assertThrows(DateTimeBuilderValidationException.class,
            () -> new DateTimeBuilder().withTimeParsingEnabled(false)
                .withDateString("2018-11-17T22:15:34.159/2018-11-17T22:15:34.159PST")
                .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
                .build());
    }

    private String format(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz"));
    }
}
