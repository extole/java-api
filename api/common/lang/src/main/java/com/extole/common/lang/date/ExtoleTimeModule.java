package com.extole.common.lang.date;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310StringParsableDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.MonthDayDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.DurationKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.MonthDayKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.PeriodKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.YearKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.YearMonthKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.ZoneIdKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.ZoneOffsetKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.MonthDaySerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearSerializer;

import com.extole.common.lang.date.deserializer.InstantDeserializer;
import com.extole.common.lang.date.deserializer.OffsetDateTimeDeserializer;
import com.extole.common.lang.date.deserializer.OffsetTimeDeserializer;
import com.extole.common.lang.date.deserializer.ZonedDateTimeDeserializer;
import com.extole.common.lang.date.deserializer.key.ExtoleTemporalKeyDeserializer;
import com.extole.common.lang.date.deserializer.key.ZonedDateTimeKeyDeserializer;
import com.extole.common.lang.date.serializer.InstantSerializer;
import com.extole.common.lang.date.serializer.OffsetDateTimeSerializer;
import com.extole.common.lang.date.serializer.OffsetTimeSerializer;
import com.extole.common.lang.date.serializer.ZonedDateTimeSerializer;
import com.extole.common.lang.date.serializer.key.ZonedDateTimeKeySerializer;

public final class ExtoleTimeModule extends SimpleModule {

    private final Supplier<Optional<ZoneId>> timeZoneSupplier;

    public ExtoleTimeModule() {
        this(() -> Optional.empty());
    }

    public ExtoleTimeModule(Supplier<Optional<ZoneId>> timeZoneSupplier) {
        super(PackageVersion.VERSION);
        this.timeZoneSupplier = timeZoneSupplier;
        configureModule();
    }

    private void configureModule() {
        // deserializers
        addDeserializer(Duration.class, DurationDeserializer.INSTANCE);
        addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
        addDeserializer(MonthDay.class, MonthDayDeserializer.INSTANCE);
        addDeserializer(Period.class, JSR310StringParsableDeserializer.PERIOD);
        addDeserializer(Year.class, YearDeserializer.INSTANCE);
        addDeserializer(YearMonth.class, YearMonthDeserializer.INSTANCE);
        addDeserializer(ZoneId.class, JSR310StringParsableDeserializer.ZONE_ID);
        addDeserializer(ZoneOffset.class, JSR310StringParsableDeserializer.ZONE_OFFSET);

        // extole custom deserializers
        addDeserializer(Instant.class, InstantDeserializer.INSTANCE);
        addDeserializer(OffsetTime.class, OffsetTimeDeserializer.INSTANCE);
        addDeserializer(OffsetDateTime.class, OffsetDateTimeDeserializer.INSTANCE);
        addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer(timeZoneSupplier));
        addDeserializer(LocalTime.class, new LocalTimeDeserializer(ExtoleDateTimeFormatters.ISO_LOCAL_TIME));
        addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(ExtoleDateTimeFormatters.ISO_LOCAL_DATE_TIME));

        // then serializers:
        addSerializer(Duration.class, DurationSerializer.INSTANCE);
        addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        addSerializer(MonthDay.class, MonthDaySerializer.INSTANCE);
        addSerializer(Period.class, new ToStringSerializer(Period.class));
        addSerializer(Year.class, YearSerializer.INSTANCE);
        addSerializer(YearMonth.class, YearMonthSerializer.INSTANCE);
        addSerializer(ZoneId.class, new ToStringSerializer(ZoneId.class));
        addSerializer(ZoneOffset.class, new ToStringSerializer(ZoneOffset.class));

        // extole custom serializers
        addSerializer(Instant.class, InstantSerializer.INSTANCE);
        addSerializer(OffsetTime.class, OffsetTimeSerializer.INSTANCE);
        addSerializer(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE);
        addSerializer(ZonedDateTime.class, ZonedDateTimeSerializer.INSTANCE);
        addSerializer(LocalTime.class, new LocalTimeSerializer(ExtoleDateTimeFormatters.ISO_LOCAL_TIME));
        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(ExtoleDateTimeFormatters.ISO_LOCAL_DATE_TIME));

        // key serializers
        // extole custom serializers
        addKeySerializer(ZonedDateTime.class, ZonedDateTimeKeySerializer.INSTANCE);

        // key deserializers
        addKeyDeserializer(Duration.class, DurationKeyDeserializer.INSTANCE);
        addKeyDeserializer(LocalDate.class, LocalDateKeyDeserializer.INSTANCE);
        addKeyDeserializer(MonthDay.class, MonthDayKeyDeserializer.INSTANCE);
        addKeyDeserializer(Period.class, PeriodKeyDeserializer.INSTANCE);
        addKeyDeserializer(Year.class, YearKeyDeserializer.INSTANCE);
        addKeyDeserializer(YearMonth.class, YearMonthKeyDeserializer.INSTANCE);
        addKeyDeserializer(ZoneId.class, ZoneIdKeyDeserializer.INSTANCE);
        addKeyDeserializer(ZoneOffset.class, ZoneOffsetKeyDeserializer.INSTANCE);

        // extole custom key deserializers
        addKeyDeserializer(Instant.class, ExtoleTemporalKeyDeserializer.INSTANT_KEY_DESERIALIZER);
        addKeyDeserializer(OffsetTime.class, ExtoleTemporalKeyDeserializer.OFFSET_TIME_KEY_DESERIALIZER);
        addKeyDeserializer(OffsetDateTime.class, ExtoleTemporalKeyDeserializer.OFFSET_DATE_TIME_KEY_DESERIALIZER);
        addKeyDeserializer(LocalTime.class, ExtoleTemporalKeyDeserializer.LOCAL_TIME_KEY_DESERIALIZER);
        addKeyDeserializer(LocalDateTime.class, ExtoleTemporalKeyDeserializer.LOCAL_DATE_TIME_KEY_DESERIALIZER);
        addKeyDeserializer(ZonedDateTime.class, new ZonedDateTimeKeyDeserializer(timeZoneSupplier));
    }
}
