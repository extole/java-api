package com.extole.api.impl.event;

import com.extole.api.event.InputEventLabel;
import com.extole.common.lang.ToString;

public final class InputEventLabelImpl implements InputEventLabel {

    private final String name;
    private final boolean required;

    public InputEventLabelImpl(String name,
        boolean required) {
        this.name = name;
        this.required = required;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
