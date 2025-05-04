package com.extole.api.impl.event;

import com.extole.api.event.EventData;
import com.extole.common.lang.ToString;

public class EventDataImpl implements EventData {
    private final String name;
    private final Object value;
    private final EventData.Source source;
    private final boolean verified;

    public EventDataImpl(String name, Object value, EventData.Source source, boolean verified) {
        this.name = name;
        this.value = value;
        this.source = source;
        this.verified = verified;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public EventData.Source getSource() {
        return source;
    }

    @Override
    public boolean isVerified() {
        return verified;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
