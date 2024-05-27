package com.extole.api.event;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.geoip.GeoIp;

@Schema
public interface EventContext {

    GeoIp[] getSourceGeoIps();

    @Nullable
    String getAppType();

    @Nullable
    String getUserId();
}
