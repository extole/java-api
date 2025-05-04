package com.extole.api.impl.event.geoip;

import com.extole.common.ip.City;

public class CityImpl implements com.extole.api.event.geoip.City {

    private final City city;

    public CityImpl(City city) {
        this.city = city;
    }

    @Override
    public String getName() {
        return city.getName();
    }
}
