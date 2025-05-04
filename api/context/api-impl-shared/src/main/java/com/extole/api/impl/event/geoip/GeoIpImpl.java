package com.extole.api.impl.event.geoip;

import javax.annotation.Nullable;

import com.extole.api.event.geoip.City;
import com.extole.api.event.geoip.Country;
import com.extole.api.event.geoip.Location;
import com.extole.api.event.geoip.State;
import com.extole.common.ip.GeoIp;

public class GeoIpImpl implements com.extole.api.event.geoip.GeoIp {

    private final GeoIp geoIp;

    public GeoIpImpl(GeoIp geoIp) {
        this.geoIp = geoIp;
    }

    @Override
    public String getIpAddress() {
        return geoIp.getIp().getValue();
    }

    @Nullable
    @Override
    public Country getCountry() {
        return geoIp.getCountry().map(country -> new CountryImpl(country)).orElse(null);
    }

    @Nullable
    @Override
    public State getState() {
        return geoIp.getState().map(state -> new StateImpl(state)).orElse(null);
    }

    @Nullable
    @Override
    public City getCity() {
        return geoIp.getCity().map(city -> new CityImpl(city)).orElse(null);
    }

    @Nullable
    @Override
    public Location getLocation() {
        return geoIp.getLocation().map(location -> new LocationImpl(location)).orElse(null);
    }

    @Nullable
    @Override
    public String getZipCode() {
        return geoIp.getZipCode().orElse(null);
    }

    @Nullable
    @Override
    public Integer getAccuracyRadiusKm() {
        return geoIp.getAccuracyRadiusKm().orElse(null);
    }

}
