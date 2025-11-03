package com.extole.dewey.decimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class DeweyDecimalInvalidFormatException extends InvalidFormatException {

    public DeweyDecimalInvalidFormatException(JsonParser jsonParser, String message, Object value,
        Class<?> targetType) {
        super(jsonParser, message, value, targetType);
    }

}
