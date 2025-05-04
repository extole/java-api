package com.extole.common.rest.timezone;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Supplier;

import javax.ws.rs.ext.ParamConverter;

import org.glassfish.jersey.server.internal.LocalizationMessages;

import com.extole.common.lang.date.DateTimeBuilder;
import com.extole.common.lang.date.DateTimeBuilderValidationException;

final class ZonedDateTimeParamConverter implements ParamConverter<ZonedDateTime> {

    private final Supplier<Optional<ZoneId>> timeZoneSupplier;

    ZonedDateTimeParamConverter(Supplier<Optional<ZoneId>> timeZoneSupplier) {
        this.timeZoneSupplier = timeZoneSupplier;
    }

    @Override
    public ZonedDateTime fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
        }

        try {
            return new DateTimeBuilder()
                .withDateString(value)
                .withDefaultTimezone(timeZoneSupplier.get().orElse(ZoneId.of("UTC")))
                .build();
        } catch (DateTimeBuilderValidationException e) {
            throw new ZonedDateTimeParamException(e, null, value);
        }
    }

    @Override
    public String toString(ZonedDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
        }
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME
            .withZone(timeZoneSupplier.get().orElse(ZoneId.of("UTC")))
            .format(value);
    }
}
