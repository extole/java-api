package com.extole.evaluateable.handlebars.helpers;

import java.io.IOException;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public enum RawBlockHelper implements Helper<Object> {
    INSTANCE;

    private static final String HELPER_NAME = "raw";

    @Override
    public CharSequence apply(Object context, Options options) throws IOException {
        return options.fn();
    }

    public String getName() {
        return HELPER_NAME;
    }
}
