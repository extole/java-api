package com.extole.client.rest.migration;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CustomMigrationRestException extends ExtoleRestException {

    public static final ErrorCode<CustomMigrationRestException> UNKNOWN_MIGRATION = new ErrorCode<>(
        "unknown_migration", 400, "Migration is not recognized", "reference");

    public static final ErrorCode<CustomMigrationRestException> CAPACITY_LIMIT = new ErrorCode<>(
        "capacity_limit", 400, "Client id queue size is 1000");

    public CustomMigrationRestException(String uniqueId, ErrorCode<CustomMigrationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
