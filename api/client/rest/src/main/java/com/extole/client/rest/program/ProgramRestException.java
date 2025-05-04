package com.extole.client.rest.program;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ProgramRestException extends ExtoleRestException {

    public static final ErrorCode<ProgramRestException> INVALID_PROGRAM =
        new ErrorCode<>("invalid_program", 400, "Invalid Program");

    public static final ErrorCode<ProgramRestException> PROGRAM_DOMAIN_NOT_FOUND =
        new ErrorCode<>("program_domain_not_found", 400, "Program domain not found", "program_id");

    public static final ErrorCode<ProgramRestException> UNKNOWN_PROGRAM =
        new ErrorCode<>("unknown_program", 400, "The provided program url is not associated with an existing program",
            "program_url");

    public ProgramRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
