package com.extole.common.lang.date.deserializer;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;

import com.extole.common.lang.date.DateTimeBuilder;
import com.extole.common.lang.date.DateTimeBuilderValidationException;
import com.extole.common.lang.date.ZonedDateTimeDeserializationException;
import com.extole.common.lang.deserializer.CacheableStdDeserializer;

public final class ZonedDateTimeDeserializer extends CacheableStdDeserializer<ZonedDateTime> {

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final InstantDeserializer<ZonedDateTime> BASE_DESERIALIZER = InstantDeserializer.ZONED_DATE_TIME;

    private final Supplier<Optional<ZoneId>> timeZoneSupplier;

    public ZonedDateTimeDeserializer(Supplier<Optional<ZoneId>> timeZoneSupplier) {
        super(ZonedDateTime.class);
        this.timeZoneSupplier = timeZoneSupplier;
    }

    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            return Optional.ofNullable(BASE_DESERIALIZER.deserialize(parser, context))
                .map(result -> result.truncatedTo(ChronoUnit.MILLIS)).orElse(null);
        } catch (IOException e) {
            String dateString = parser.getText().trim();
            try {
                return new DateTimeBuilder()
                    .withDateString(dateString)
                    .withDefaultTimezone(timeZoneSupplier.get().orElse(UTC))
                    .build().truncatedTo(ChronoUnit.MILLIS);
            } catch (DateTimeBuilderValidationException ex) {
                throw new ZonedDateTimeDeserializationException(dateString, e);
            }
        }
    }
}
