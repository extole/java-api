package com.extole.evaluateable.handlebars.helpers;

import java.io.IOException;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import com.extole.encoder.Encoder;

public enum SafeUriComponentHelper implements Helper<String> {
    INSTANCE;

    private static final String HELPER_NAME = "safeUriComponent";

    @Override
    public CharSequence apply(String context, Options options) throws IOException {
        return Encoder.getInstance().safeUriComponent(context);
    }

    public String getName() {
        return HELPER_NAME;
    }
}
