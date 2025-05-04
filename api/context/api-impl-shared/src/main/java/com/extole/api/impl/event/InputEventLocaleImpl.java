package com.extole.api.impl.event;

import java.util.Optional;

import com.extole.api.event.InputEventLocale;
import com.extole.common.lang.ToString;

public final class InputEventLocaleImpl implements InputEventLocale {

    private final Optional<String> userSpecified;
    private final Optional<String> lastBrowser;

    public InputEventLocaleImpl(Optional<String> userSpecified, Optional<String> lastBrowser) {
        this.userSpecified = userSpecified;
        this.lastBrowser = lastBrowser;
    }

    @Override
    public String getUserSpecified() {
        return userSpecified.orElse(null);
    }

    @Override
    public String getLastBrowser() {
        return lastBrowser.orElse(null);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
