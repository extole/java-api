package com.extole.api.service;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface DateService {

    Date parse(String format, String date) throws InvalidDateException;

    Date parse(String date) throws InvalidDateException;

    String now();

    /**
     *  Same as {@link #toClientTimezone(String, boolean)} invoked with sameLocal = false
     */
    String toClientTimezone(String dateTime) throws InvalidDateException;

    /**
     *  Same as {@link #toTimezone(String, String, boolean)} invoked with Client's timezone
     */
    String toClientTimezone(String dateTime, boolean sameLocal) throws InvalidDateException;

    /**
     *  Same as {@link #toTimezone(String, String, boolean)} invoked with sameLocal = false
     */
    String toTimezone(String dateTime, String timezone) throws InvalidDateException, InvalidTimezoneException;

    /**
     *
     * <p>
     * When invoked with sameLocal=false, this method changes the time-zone and retains the instant.
     * This normally results in a change to the local date-time.
     * <p>
     * <p>
     * When invoked with sameLocal=true, this method changes the time-zone and retains the local date-time.
     * <p>
     *
     * @param dateTime a {@code String} based date with or without a timezone
     * @param timezone a {@code String} based requested timezone
     * @param sameLocal instruction to preserve the local date-time or not
     * @return a {@code String} based on this date-time with the requested timezone, not null
     * @throws InvalidDateException if the dateTime is invalid or has an unsupported format
     * @throws InvalidTimezoneException if the requested timezone is invalid
     */
    String toTimezone(String dateTime, String timezone, boolean sameLocal) throws InvalidDateException,
        InvalidTimezoneException;

}
