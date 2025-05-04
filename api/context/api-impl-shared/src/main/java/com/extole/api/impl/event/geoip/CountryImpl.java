package com.extole.api.impl.event.geoip;

public class CountryImpl implements com.extole.api.event.geoip.Country {

    private final String isoCode;
    private final String name;

    public CountryImpl(com.extole.common.ip.Country country) {
        this.isoCode = country.getIsoCode();
        this.name = country.getName();
    }

    public CountryImpl(String isoCode, String name) {
        this.isoCode = isoCode;
        this.name = name;
    }

    @Override
    public String getIsoCode() {
        return isoCode;
    }

    @Override
    public String getName() {
        return name;
    }
}
