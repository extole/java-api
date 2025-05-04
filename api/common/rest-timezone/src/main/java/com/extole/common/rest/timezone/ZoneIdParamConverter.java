package com.extole.common.rest.timezone;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;

import javax.ws.rs.ext.ParamConverter;

import org.glassfish.jersey.server.internal.LocalizationMessages;

final class ZoneIdParamConverter implements ParamConverter<ZoneId> {

    private static final int ISO_8601_TIMEZONE_MAX_LENGTH = 6;

    @Override
    public ZoneId fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
        }

        ZoneId timeZone = ZoneId.of(value);
        if (timeZone instanceof ZoneOffset) {
            validate((ZoneOffset) timeZone);
        }

        return timeZone;
    }

    private static void validate(ZoneOffset timeZone) {
        if (timeZone.getId().length() > ISO_8601_TIMEZONE_MAX_LENGTH) {
            throw new DateTimeException("Invalid ID for ZoneOffset, seconds are not supported");
        }
    }

    @Override
    public String toString(ZoneId value) {
        if (value == null) {
            throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
        }
        return value.getId();
    }
}
