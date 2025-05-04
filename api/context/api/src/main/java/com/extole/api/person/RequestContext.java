package com.extole.api.person;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.geoip.GeoIp;

@Schema
public interface RequestContext {

    @Deprecated // TODO remove in favor of getGeoIp().getIpAddress(), ENG-10118
    String getIp();

    String getDeviceId();

    String getCreatedAt();

    GeoIp getGeoIp();
}
