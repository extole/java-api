package com.extole.api.impl.person;

import com.extole.api.event.geoip.GeoIp;
import com.extole.api.impl.event.geoip.GeoIpImpl;
import com.extole.api.person.RequestContext;
import com.extole.common.lang.ToString;
import com.extole.person.service.profile.request.context.Location;

public class RequestContextImpl implements RequestContext {
    private final Location location;

    public RequestContextImpl(Location location) {
        this.location = location;
    }

    @Override
    public String getIp() {
        return location.getGeoIp().getIp().getValue();
    }

    @Override
    public String getDeviceId() {
        return location.getDeviceId().getId();
    }

    @Override
    public String getCreatedAt() {
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
