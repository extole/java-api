package com.extole.dewey.decimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class DeweyDecimalMismatchedInputException extends MismatchedInputException {

    public DeweyDecimalMismatchedInputException(JsonParser jsonParser, String message) {
        super(jsonParser, message);
    }

}
