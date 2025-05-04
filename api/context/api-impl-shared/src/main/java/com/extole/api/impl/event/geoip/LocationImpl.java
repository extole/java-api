package com.extole.api.impl.event.geoip;

import java.util.Optional;

import javax.annotation.Nullable;

import com.extole.common.ip.Location;

public class LocationImpl implements com.extole.api.event.geoip.Location {

    private final Optional<Double> latitude;
    private final Optional<Double> longitude;

    public LocationImpl(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public LocationImpl(Double latitude, Double longitude) {
        this.latitude = Optional.ofNullable(latitude);
        this.longitude = Optional.ofNullable(longitude);
    }

    @Nullable
    @Override
    public Double getLatitude() {
        return latitude.orElse(null);
    }

    @Nullable
    @Override
    public Double getLongitude() {
        return longitude.orElse(null);
    }
}
