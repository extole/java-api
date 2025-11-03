package com.extole.common.lang.date;

import java.time.ZoneId;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class DateTimeRangeBuilder {

    public static final String RANGE_SEPARATOR = "/";
    private static final String LEGACY_RANGE_SEPARATOR = "~";
    private final DateTimeBuilder startDateTimeBuilder;
    private final DateTimeBuilder endDateTimeBuilder;
    private String dateTimeRangeString;

    public DateTimeRangeBuilder() {
        this.startDateTimeBuilder = new DateTimeBuilder();
        this.endDateTimeBuilder = new DateTimeBuilder();
    }

    public DateTimeRangeBuilder withRangeString(String dateTimeRange) {
        this.dateTimeRangeString = dateTimeRange;
        return this;
    }

    public DateTimeRangeBuilder withDefaultTimezone(ZoneId timezone) {
        startDateTimeBuilder.withDefaultTimezone(timezone);
        endDateTimeBuilder.withDefaultTimezone(timezone);
        return this;
    }

    public DateTimeRangeBuilder withTimeParsingEnabled(boolean timeParsingEnabled) {
        startDateTimeBuilder.withTimeParsingEnabled(timeParsingEnabled);
        endDateTimeBuilder.withTimeParsingEnabled(timeParsingEnabled);
        return this;
    }

    public DateTimeRange build() throws DateTimeBuilderValidationException {
        if (Strings.isNullOrEmpty(dateTimeRangeString)) {
            throw new DateTimeBuilderValidationException("Range string is missing or empty.");
        }

        String separator;
        if (dateTimeRangeString.contains(LEGACY_RANGE_SEPARATOR)) {
            separator = LEGACY_RANGE_SEPARATOR;
        } else {
            separator = RANGE_SEPARATOR;
        }

        List<DateTimeBuilderValidationException> exceptions = Lists.newArrayList();
        String[] rangeComponents = dateTimeRangeString.split(separator);
        for (int i = 0; i < rangeComponents.length; i++) {
            try {
                startDateTimeBuilder.withDateString(concatenateComponents(rangeComponents, 0, i));
                endDateTimeBuilder
                    .withDateString(concatenateComponents(rangeComponents, i + 1, rangeComponents.length - 1));

                return new DateTimeRange(startDateTimeBuilder.build(), endDateTimeBuilder.build());
            } catch (DateTimeBuilderValidationException e) {
                exceptions.add(e);
            }
        }

        DateTimeBuilderValidationException validationException = new DateTimeBuilderValidationException(
            "Range string does not correspond to any of the allowed formats.");
        exceptions.forEach(validationException::addSuppressed);
        throw validationException;
    }

    private String concatenateComponents(String[] rangeComponents, int startComponentIndex, int endComponentIndex) {
        return Joiner.on("/")
            .join(Lists.newArrayList(rangeComponents).subList(startComponentIndex, endComponentIndex + 1));
    }
}
