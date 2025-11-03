package com.extole.common.lang.date.deserializer.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.DeserializationContext;

import com.extole.common.lang.date.DateTimeBuilder;
import com.extole.common.lang.date.DateTimeBuilderValidationException;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;
import com.extole.common.lang.date.ZonedDateTimeDeserializationException;

public final class ZonedDateTimeKeyDeserializer extends ExtoleTemporalKeyDeserializer<ZonedDateTime> {

    private static final ZoneId UTC = ZoneId.of("UTC");

    private final Supplier<Optional<ZoneId>> timeZoneSupplier;

    public ZonedDateTimeKeyDeserializer(Supplier<Optional<ZoneId>> timeZoneSupplier) {
        super((key, context) -> {
            try {
                return ZonedDateTime.parse(key, ExtoleDateTimeFormatters.ISO_ZONED_DATE_TIME);
            } catch (DateTimeException e) {
                return handleDateTimeException(context, ZonedDateTime.class, e, key);
            }
        });
        this.timeZoneSupplier = timeZoneSupplier;
    }

    @Override
    public ZonedDateTime deserializeKey(String key, DeserializationContext context) throws IOException {
        try {
            return super.deserializeKey(key, context);
        } catch (IOException e) {
            return buildDateFromString(key, timeZoneSupplier);
        }
    }

    private static ZonedDateTime buildDateFromString(String dateString, Supplier<Optional<ZoneId>> timeZoneSupplier)
        throws ZonedDateTimeDeserializationException {
        try {
            return new DateTimeBuilder()
                .withDateString(dateString)
                .withDefaultTimezone(timeZoneSupplier.get().orElse(UTC))
                .build().truncatedTo(ChronoUnit.MILLIS);
        } catch (DateTimeBuilderValidationException e) {
            throw new ZonedDateTimeDeserializationException(dateString, e);
        }
    }
}
