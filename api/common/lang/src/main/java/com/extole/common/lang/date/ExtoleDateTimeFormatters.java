package com.extole.common.lang.date;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public final class ExtoleDateTimeFormatters {

    private static final int TIME_FIELDS_WIDTH = 2;
    private static final int TIME_FRACTION_MIN_WIDTH = 0;
    private static final int TIME_FRACTION_MAX_WIDTH = 9;

    public static final DateTimeFormatter ISO_LOCAL_TIME = new DateTimeFormatterBuilder()
        .appendValue(HOUR_OF_DAY, TIME_FIELDS_WIDTH)
        .appendLiteral(':')
        .appendValue(MINUTE_OF_HOUR, TIME_FIELDS_WIDTH)
        .optionalStart()
        .appendLiteral(':')
        .appendValue(SECOND_OF_MINUTE, TIME_FIELDS_WIDTH)
        .optionalStart()
        .appendFraction(MILLI_OF_SECOND, TIME_FRACTION_MIN_WIDTH, TIME_FRACTION_MAX_WIDTH, true)
        .toFormatter();

    public static final DateTimeFormatter ISO_OFFSET_TIME = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(ISO_LOCAL_TIME)
        .appendOffsetId()
        .toFormatter();

    public static final DateTimeFormatter ISO_LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral('T')
        .append(ISO_LOCAL_TIME)
        .toFormatter();

    public static final DateTimeFormatter ISO_OFFSET_DATE_TIME = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(ISO_LOCAL_DATE_TIME)
        .appendOffsetId()
        .toFormatter();

    public static final DateTimeFormatter ISO_ZONED_DATE_TIME = new DateTimeFormatterBuilder()
        .append(ISO_OFFSET_DATE_TIME)
        .optionalStart()
        .appendLiteral('[')
        .parseCaseSensitive()
        .appendZoneRegionId()
        .appendLiteral(']')
        .toFormatter();

    public static final DateTimeFormatter ISO_INSTANT = new DateTimeFormatterBuilder()
        .appendInstant()
        .toFormatter();

    private ExtoleDateTimeFormatters() {
    }
}
