package com.extole.api.impl.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.extole.api.service.DateService;
import com.extole.api.service.InvalidDateException;
import com.extole.api.service.InvalidTimezoneException;
import com.extole.common.lang.date.DateTimeBuilder;
import com.extole.common.lang.date.DateTimeBuilderValidationException;

public class DateServiceImpl implements DateService {

    private final ZoneId clientTimezone;

    public DateServiceImpl(ZoneId clientTimezone) {
        this.clientTimezone = clientTimezone;
    }

    @Override
    public Date parse(String format, String date) throws InvalidDateException {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            throw new InvalidDateException(String.format("Invalid date / format: %s / %s ", date, format), e);
        }
    }

    @Override
    public Date parse(String date) throws InvalidDateException {
        try {
            return SimpleDateFormat.getDateInstance().parse(date);
        } catch (ParseException e) {
            throw new InvalidDateException(String.format("Invalid date " + date), e);
        }
    }

    @Override
    public String now() {
        return Instant.now().atZone(clientTimezone).toString();
    }

    @Override
    public String toClientTimezone(String dateTime) throws InvalidDateException {
        return createZonedDateTime(dateTime).withZoneSameInstant(clientTimezone).toString();
    }

    @Override
    public String toClientTimezone(String dateTime, boolean sameLocal) throws InvalidDateException {
        if (sameLocal) {
            return createZonedDateTime(dateTime).withZoneSameLocal(clientTimezone).toString();
        } else {
            return toClientTimezone(dateTime);
        }
    }

    @Override
    public String toClientTimezone(String dateTime, String format) throws InvalidDateException {
        return createZonedDateTime(dateTime).withZoneSameInstant(clientTimezone)
            .format(DateTimeFormatter.ofPattern(format));
    }

    @Override
    public String toTimezone(String dateTime, String timezone)
        throws InvalidDateException, InvalidTimezoneException {
        return toZoneDateTimeAtTimezone(dateTime, timezone).toString();
    }

    @Override
    public String toTimezone(String dateTime, String timezone, boolean sameLocal)
        throws InvalidDateException, InvalidTimezoneException {
        if (sameLocal) {
            try {
                return createZonedDateTime(dateTime).withZoneSameLocal(ZoneId.of(timezone)).toString();
            } catch (DateTimeException e) {
                throw new InvalidTimezoneException("Invalid timezone " + timezone, e);
            }
        } else {
            return toTimezone(dateTime, timezone);
        }
    }

    @Override
    public String toTimezone(String dateTime, String format, String timezone)
        throws InvalidDateException, InvalidTimezoneException {
        return toZoneDateTimeAtTimezone(dateTime, timezone).format(DateTimeFormatter.ofPattern(format));
    }

    private ZonedDateTime createZonedDateTime(String dateTime) throws InvalidDateException {
        try {
            return new DateTimeBuilder()
                .withDateString(dateTime)
                .withDefaultTimezone(clientTimezone)
                .build();
        } catch (DateTimeBuilderValidationException e) {
            throw new InvalidDateException("Invalid date " + dateTime, e);
        }
    }

    private ZonedDateTime toZoneDateTimeAtTimezone(String dateTime, String timezone)
        throws InvalidDateException, InvalidTimezoneException {
        try {
            return createZonedDateTime(dateTime).withZoneSameInstant(ZoneId.of(timezone));
        } catch (DateTimeException e) {
            throw new InvalidTimezoneException("Invalid timezone " + timezone, e);
        }
    }
}
