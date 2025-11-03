package com.extole.common.event.migration;

import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import com.extole.common.lang.date.ExtoleTimeModule;

public final class MigrationObjectMapper {

    // TODO replace with event migrators ENG-14892
    static {
        try {
            ZoneRulesProvider.getVersions("US/Pacific-New");
        } catch (ZoneRulesException e) {
            ZoneRulesProvider.registerProvider(new UsPacificNewLegacyZoneRulesProvider());
        }
    }

    public static final ObjectMapper OBJECT_MAPPER =
        new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .registerModule(new ExtoleTimeModule())
            .registerModule(new Jdk8Module())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private MigrationObjectMapper() {
    }

}
