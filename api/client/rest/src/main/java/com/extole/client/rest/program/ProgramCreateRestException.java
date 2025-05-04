package com.extole.client.rest.program;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ProgramCreateRestException extends ExtoleRestException {

    public static final ErrorCode<ProgramCreateRestException> INVALID_URI_SYNTAX =
        new ErrorCode<>("invalid_uri_syntax", 403, "A uri supplied has syntax errors");

    public static final ErrorCode<ProgramCreateRestException> DUPLICATE_PROGRAM_DOMAIN =
        new ErrorCode<>("duplicate_program_domain", 403, "The program domain already exists", "program_domain");

    public ProgramCreateRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
