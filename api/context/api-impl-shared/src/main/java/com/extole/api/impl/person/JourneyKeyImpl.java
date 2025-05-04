package com.extole.api.impl.person;

import com.extole.api.person.JourneyKey;

public class JourneyKeyImpl implements JourneyKey {

    private final String value;
    private final String name;

    public JourneyKeyImpl(com.extole.person.service.profile.journey.JourneyKey journeyKey) {
        this.name = journeyKey.getName();
        this.value = journeyKey.getValue();
    }

    public JourneyKeyImpl(String name, String value) {
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
