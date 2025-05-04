package com.extole.api.event.geoip;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface GeoIp {

    String getIpAddress();

    @Nullable
    Country getCountry();

    @Nullable
    State getState();

    @Nullable
    City getCity();

    @Nullable
    Location getLocation();

    @Nullable
    String getZipCode();

    @Nullable
    Integer getAccuracyRadiusKm();
}
