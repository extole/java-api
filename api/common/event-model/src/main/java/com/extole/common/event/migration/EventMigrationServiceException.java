package com.extole.common.event.migration;

public class EventMigrationServiceException extends Exception {
    public EventMigrationServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventMigrationServiceException(String message) {
        super(message);
    }
}
