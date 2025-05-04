package com.extole.api.service;

public class InvalidMonthDayException extends Exception {

    public InvalidMonthDayException(String message) {
        super(message);
    }

    public InvalidMonthDayException(String message, Throwable cause) {
        super(message, cause);
    }

}
