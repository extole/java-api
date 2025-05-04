package com.extole.api.impl.event.step;

import com.extole.api.event.step.PartnerEventId;

public class PartnerEventIdImpl implements PartnerEventId {

    private final String name;
    private final String value;

    public PartnerEventIdImpl(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

}
