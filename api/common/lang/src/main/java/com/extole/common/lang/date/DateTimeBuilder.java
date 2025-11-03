package com.extole.common.lang.date;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeBuilder.class);

    private static final String TIME_PATTERN =
        "yyyy[-M[-d['T'[HH[:mm[:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]" +
            "]]]]]][[OOOO][O][z][Z][x][XXXXX][XXXX]['['VV']']]";
    private static final DateTimeFormatter FORMATTER_WITH_TIME_AND_ZONE = DateTimeFormatter.ofPattern(TIME_PATTERN);

    @Deprecated // TODO Remove when no longer used ENG-8559
    private static final String DEPRECATED_TIME_PATTERN =
        "yyyy[-M[-d[' '[HH[:mm[:ss[.SSS]]]]]]][[OOOO][O][z][XXXXX][XXXX]['['VV']']]";
    private static final DateTimeFormatter DEPRECATED_FORMATTER_WITH_TIME_AND_ZONE =
        DateTimeFormatter.ofPattern(DEPRECATED_TIME_PATTERN);

    private String dateTimeString;
    private ZoneId defaultTimezone;
    private boolean timeParsingEnabled = true;

    public DateTimeBuilder withDateString(String dateTime) {
        this.dateTimeString = dateTime;
        return this;
    }

    public DateTimeBuilder withDefaultTimezone(ZoneId timezone) {
        this.defaultTimezone = timezone;
        return this;
    }

    public DateTimeBuilder withTimeParsingEnabled(boolean timeParsingEnabled) {
        this.timeParsingEnabled = timeParsingEnabled;
        return this;
    }

    public ZonedDateTime build() throws DateTimeBuilderValidationException, DateTimeException {
        validate();

        return new Processor().process();
    }

    private void validate() throws DateTimeBuilderValidationException {
        if (Strings.isNullOrEmpty(this.dateTimeString)) {
            throw new DateTimeBuilderValidationException("Datetime string is missing or empty.");
        }

        if (this.defaultTimezone == null) {
            throw new DateTimeBuilderValidationException("Default timezone is missing.");
        }
    }

    private final class Processor {
        private int year;
        private int month;
        private int day;
        private int hours;
        private int minutes;
        private int seconds;
        private int nanoseconds;
        private boolean isFurtherParsingRequired = true;

        private ZonedDateTime process() throws DateTimeBuilderValidationException, DateTimeException {
            TemporalAccessor temporalAccessor;
            try {
                temporalAccessor = FORMATTER_WITH_TIME_AND_ZONE.parse(dateTimeString);
            } catch (DateTimeParseException e) {
                try {
                    temporalAccessor = DEPRECATED_FORMATTER_WITH_TIME_AND_ZONE.parse(dateTimeString);
                    LOG.warn("Deprecated non ISO data/time format specified: {}", dateTimeString);
                } catch (DateTimeParseException e1) {
                    throw new DateTimeBuilderValidationException(
                        "Input value " + dateTimeString + " does not correspond to format " + TIME_PATTERN);
                }
            }

            parseChronoField(temporalAccessor, ChronoField.YEAR);
            parseChronoField(temporalAccessor, ChronoField.MONTH_OF_YEAR);
            parseChronoField(temporalAccessor, ChronoField.DAY_OF_MONTH);
            if (timeParsingEnabled) {
                parseChronoField(temporalAccessor, ChronoField.HOUR_OF_DAY);
                parseChronoField(temporalAccessor, ChronoField.MINUTE_OF_HOUR);
                parseChronoField(temporalAccessor, ChronoField.SECOND_OF_MINUTE);
                parseChronoField(temporalAccessor, ChronoField.NANO_OF_SECOND);
            } else {
                this.hours = (int) ChronoField.HOUR_OF_DAY.range().getMinimum();
                this.minutes = (int) ChronoField.MINUTE_OF_HOUR.range().getMinimum();
                this.seconds = (int) ChronoField.SECOND_OF_MINUTE.range().getMinimum();
                this.nanoseconds = (int) ChronoField.NANO_OF_SECOND.range().getMinimum();
            }

            ZoneId timezone = parseZoneId(temporalAccessor);

            return ZonedDateTime.of(this.year, this.month, this.day, this.hours, this.minutes, this.seconds,
                this.nanoseconds, timezone);
        }

        private void parseChronoField(TemporalAccessor temporalAccessor, ChronoField chronoField) {
            int chronoFieldValue = 0;
            if (this.isFurtherParsingRequired) {
                try {
                    chronoFieldValue = temporalAccessor.get(chronoField);
                } catch (DateTimeException e) {
                    this.isFurtherParsingRequired = false;
                }
            }

            if (!this.isFurtherParsingRequired) {
                chronoFieldValue = (int) chronoField.range().getMinimum();
            }

            setValue(chronoField, chronoFieldValue);
        }

        private void setValue(ChronoField chronoField, int value) {
            switch (chronoField) {
                case YEAR:
                    this.year = value;
                    break;
                case MONTH_OF_YEAR:
                    this.month = value;
                    break;
                case DAY_OF_MONTH:
                    this.day = value;
                    break;
                case HOUR_OF_DAY:
                    this.hours = value;
                    break;
                case MINUTE_OF_HOUR:
                    this.minutes = value;
                    break;
                case SECOND_OF_MINUTE:
                    this.seconds = value;
                    break;
                case NANO_OF_SECOND:
                    this.nanoseconds = value;
                    break;
                default:
                    throw new DateTimeBuilderRuntimeException("Unsupported field " + chronoField);
            }
        }

        private ZoneId parseZoneId(TemporalAccessor temporalAccessor) {
            try {
                return temporalAccessor.query(ZoneId::from);
            } catch (DateTimeException e) {
                return defaultTimezone;
            }
        }
    }
}
