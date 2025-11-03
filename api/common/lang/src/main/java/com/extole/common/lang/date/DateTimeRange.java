package com.extole.common.lang.date;

import java.time.ZonedDateTime;
import java.util.Objects;

public class DateTimeRange {
    private static final String DATE_TIME_RANGE_SEPARATOR = "/";
    private final ZonedDateTime startDate;
    private final ZonedDateTime endDate;

    public DateTimeRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        DateTimeRange that = (DateTimeRange) object;
        return Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    @Override
    public String toString() {
        return startDate.toInstant().toString() + DATE_TIME_RANGE_SEPARATOR + endDate.toInstant().toString();
    }
}
