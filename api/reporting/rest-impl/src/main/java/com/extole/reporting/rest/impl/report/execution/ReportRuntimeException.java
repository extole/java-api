package com.extole.reporting.rest.impl.report.execution;

public class ReportRuntimeException extends RuntimeException {

    public ReportRuntimeException(Throwable cause) {
        super(cause);
    }

    public ReportRuntimeException(String message) {
        super(message);
    }
}
