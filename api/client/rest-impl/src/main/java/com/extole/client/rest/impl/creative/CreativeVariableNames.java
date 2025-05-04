package com.extole.client.rest.impl.creative;

public enum CreativeVariableNames {

    DELAY_PERIOD("delayPeriod");

    private final String name;

    CreativeVariableNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
