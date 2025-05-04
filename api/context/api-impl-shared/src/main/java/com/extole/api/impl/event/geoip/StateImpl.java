package com.extole.api.impl.event.geoip;

public class StateImpl implements com.extole.api.event.geoip.State {

    private final String isoCode;
    private final String name;

    public StateImpl(com.extole.common.ip.State state) {
        this.isoCode = state.getIsoCode();
        this.name = state.getName();
    }

    public StateImpl(String isoCode, String name) {
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
