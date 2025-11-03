package com.extole.common.lang.date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

public class DateTimeRangeBuilderTest {

    @Test
    public void testDateTimeRange() throws Exception {
        DateTimeRange range1 = new DateTimeRangeBuilder()
            .withRangeString("2018/2019")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build();
        assertThat(format(range1.getStartDate())).isEqualTo("2018-01-01T00:00:00.000Z");
        assertThat(format(range1.getEndDate())).isEqualTo("2019-01-01T00:00:00.000Z");

        DateTimeRange range2 = new DateTimeRangeBuilder()
            .withRangeString("2018-07/2019-07")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build();
        assertThat(format(range2.getStartDate())).isEqualTo("2018-07-01T00:00:00.000Z");
        assertThat(format(range2.getEndDate())).isEqualTo("2019-07-01T00:00:00.000Z");

        DateTimeRange range3 = new DateTimeRangeBuilder()
            .withRangeString("2018-07-17/2019-07-17")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build();
        assertThat(format(range3.getStartDate())).isEqualTo("2018-07-17T00:00:00.000Z");
        assertThat(format(range3.getEndDate())).isEqualTo("2019-07-17T00:00:00.000Z");

        DateTimeRange range4 = new DateTimeRangeBuilder()
            .withRangeString("2018-07-17T22/2019-07-17T22")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build();
        assertThat(format(range4.getStartDate())).isEqualTo("2018-07-17T22:00:00.000Z");
        assertThat(format(range4.getEndDate())).isEqualTo("2019-07-17T22:00:00.000Z");

        DateTimeRange range5 = new DateTimeRangeBuilder()
            .withRangeString("2018-07-17T22:15/2019-07-17T22:15")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build();
        assertThat(format(range5.getStartDate())).isEqualTo("2018-07-17T22:15:00.000Z");
        assertThat(format(range5.getEndDate())).isEqualTo("2019-07-17T22:15:00.000Z");

        DateTimeRange range6 = new DateTimeRangeBuilder()
            .withRangeString("2018-07-17T22:15:34/2019-07-17T22:15:34")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build();
        assertThat(format(range6.getStartDate())).isEqualTo("2018-07-17T22:15:34.000Z");
        assertThat(format(range6.getEndDate())).isEqualTo("2019-07-17T22:15:34.000Z");

        DateTimeRange range7 = new DateTimeRangeBuilder()
            .withRangeString("2018-07-17T22:15:34.159/2019-07-17T22:15:34.159")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build();
        assertThat(format(range7.getStartDate())).isEqualTo("2018-07-17T22:15:34.159Z");
        assertThat(format(range7.getEndDate())).isEqualTo("2019-07-17T22:15:34.159Z");
    }

    @Test
    public void testDateTimeRangeInvalidRange() {
        assertThrows(DateTimeBuilderValidationException.class,
            () -> new DateTimeRangeBuilder()
                .withRangeString("2018-07-17T22:15:34.159/2019-07-17T22:15:34.159|2019-07-17T22:15:34.159")
                .withDefaultTimezone(ZoneOffset.UTC)
                .build());
    }

    @Test
    public void testTimeZone() throws Exception {
        DateTimeRange range1 = new DateTimeRangeBuilder()
            .withRangeString("2018PST/2019PST")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build();
        assertThat(format(range1.getStartDate())).isEqualTo("2018-01-01T00:00:00.000PST");
        assertThat(format(range1.getEndDate())).isEqualTo("2019-01-01T00:00:00.000PST");

        DateTimeRange range2 = new DateTimeRangeBuilder()
            .withRangeString("2018-07-17T22:15:34.159+08:00/2019-07-17T22:15:34.159+08:00")
            .withDefaultTimezone(ZoneOffset.UTC)
            .build();
        assertThat(format(range2.getStartDate())).isEqualTo("2018-07-17T22:15:34.159+08:00");
        assertThat(format(range2.getEndDate())).isEqualTo("2019-07-17T22:15:34.159+08:00");

        DateTimeRange range3 = new DateTimeRangeBuilder()
            .withRangeString("2018-11-17T22:15:34.159/2019-11-17T22:15:34.159")
            .withDefaultTimezone(ZoneId.of("PST", ZoneId.SHORT_IDS))
            .build();
        assertThat(format(range3.getStartDate())).isEqualTo("2018-11-17T22:15:34.159PST");
        assertThat(format(range3.getEndDate())).isEqualTo("2019-11-17T22:15:34.159PST");

        DateTimeRange range4 = new DateTimeRangeBuilder()
            .withRangeString("2018-11-17T22:15:34.159/2019-11-17T22:15:34.159")
            .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
            .build();
        assertThat(format(range4.getStartDate())).isEqualTo("2018-11-17T22:15:34.159PST");
        assertThat(format(range4.getEndDate())).isEqualTo("2019-11-17T22:15:34.159PST");
    }

    @Test
    public void testTimeZoneInvalidRange() {
        assertThrows(DateTimeBuilderValidationException.class,
            () -> new DateTimeRangeBuilder()
                .withRangeString("2018-11-17T22:15:34.159/2019-11-17T22:15:34.159|2019-07-17T22:15:34.159")
                .withDefaultTimezone(ZoneId.of("America/Los_Angeles"))
                .build());
    }

    private String format(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz"));
    }
}
