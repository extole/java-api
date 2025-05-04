package com.extole.api.impl.person;

import com.extole.api.event.geoip.GeoIp;
import com.extole.api.impl.event.geoip.GeoIpImpl;
import com.extole.api.person.Location;
import com.extole.common.lang.ToString;

public class LocationImpl implements Location {
    private final com.extole.person.service.profile.request.context.Location location;

    public LocationImpl(com.extole.person.service.profile.request.context.Location location) {
        this.location = location;
    }

    @Override
    public String getDeviceId() {
        return location.getDeviceId().getId();
    }

    @Override
    public String getCreatedDate() {
        return location.getCreatedAt().toString();
    }

    @Override
    public GeoIp getGeoIp() {
        return new GeoIpImpl(location.getGeoIp());
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
