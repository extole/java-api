package com.extole.consumer.rest.impl.report;

public class ReportNotFoundException extends Exception {

    public ReportNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportNotFoundException(String message) {
        super(message);
    }

}
