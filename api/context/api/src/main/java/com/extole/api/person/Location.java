package com.extole.api.person;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.geoip.GeoIp;

@Schema
public interface Location {

    String getDeviceId();

    String getCreatedDate();

    GeoIp getGeoIp();
}
