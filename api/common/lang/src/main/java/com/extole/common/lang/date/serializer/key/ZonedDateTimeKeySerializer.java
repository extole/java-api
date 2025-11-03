package com.extole.common.lang.date.serializer.key;

import java.io.IOException;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

import com.extole.common.lang.date.ExtoleDateTimeFormatters;

public final class ZonedDateTimeKeySerializer extends JsonSerializer<ZonedDateTime> {

    public static final ZonedDateTimeKeySerializer INSTANCE = new ZonedDateTimeKeySerializer();

    private ZonedDateTimeKeySerializer() {
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator generator, SerializerProvider serializers)
        throws IOException {
        if (serializers.isEnabled(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)) {
            generator.writeFieldName(ExtoleDateTimeFormatters.ISO_ZONED_DATE_TIME.format(value));
        } else {
            generator.writeFieldName(ExtoleDateTimeFormatters.ISO_OFFSET_DATE_TIME.format(value));
        }
    }
}
