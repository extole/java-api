package com.extole.common.lang;

import java.util.regex.Pattern;

import javax.annotation.Nullable;

public final class ExtoleNameValidator {
    private static final Pattern PATTERN_FOR_EXTOLE_VALID_NAME = Pattern.compile("[\\/\\.0-9a-zA-Z_:-]+");

    private ExtoleNameValidator() {
    }

    public static boolean isValid(@Nullable String value) {
        if (!WebStringParameterValidator.isValid(value)) {
            return false;
        }
        return PATTERN_FOR_EXTOLE_VALID_NAME.matcher(value).matches();
    }
}
