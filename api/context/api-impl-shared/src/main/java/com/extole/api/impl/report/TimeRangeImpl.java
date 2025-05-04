package com.extole.api.impl.report;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;

import com.extole.api.report.configurable.TimeRange;
import com.extole.common.lang.ToString;

public class TimeRangeImpl implements TimeRange, Serializable {

    private final String startTime;

    private final String endTime;

    private final String timezone;

    public TimeRangeImpl(Instant startTime, Instant endTime, ZoneId timeZone) {
        this.startTime = startTime.toString();
        this.endTime = endTime.toString();
        this.timezone = timeZone.toString();
    }

    @Override
    public String getStartTime() {
        return startTime;
    }

    @Override
    public String getEndTime() {
        return endTime;
    }

    @Override
    public String getTimezone() {
        return timezone;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
