package com.extole.common.lang.date;

import com.fasterxml.jackson.databind.JsonMappingException;

public class ZonedDateTimeDeserializationException extends JsonMappingException {

    private static final String ERROR_MESSAGE_PATTERN = "Could not map string: %s to datetime.";

    private final String dateString;

    public ZonedDateTimeDeserializationException(String dateString, Throwable cause) {
        super(String.format(ERROR_MESSAGE_PATTERN, dateString), cause);
        this.dateString = dateString;
    }

    public String getDateString() {
        return dateString;
    }
}
