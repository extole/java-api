package com.extole.common.lang;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

public final class WebStringParameterValidator {

    private WebStringParameterValidator() {
    }

    public static boolean isValid(@Nullable String value) {
        return StringUtils.isNotBlank(value) && !"undefined".equalsIgnoreCase(value) && !"null".equalsIgnoreCase(value);
    }

    public static boolean isValid(@Nullable Object value) {
        return value != null && (!isCharSequence(value) || isValid(value.toString()));
    }

    private static boolean isCharSequence(@Nullable Object value) {
        return value instanceof CharSequence;
    }
}
