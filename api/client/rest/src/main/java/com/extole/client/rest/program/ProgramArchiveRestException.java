package com.extole.client.rest.program;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ProgramArchiveRestException extends ExtoleRestException {

    public static final ErrorCode<ProgramArchiveRestException> LAST_PROGRAM =
        new ErrorCode<>("last_program", 400, "Cannot archive last program");

    public static final ErrorCode<ProgramArchiveRestException> LAST_EXTOLE_PROGRAM =
        new ErrorCode<>("last_extole_program", 400, "Cannot archive last Extole program");

    public static final ErrorCode<ProgramArchiveRestException> CANNOT_ARCHIVE_VALID_PROGRAM =
        new ErrorCode<>("cannot_archive_valid_program", 400, "Cannot archive a valid program");

    public ProgramArchiveRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
