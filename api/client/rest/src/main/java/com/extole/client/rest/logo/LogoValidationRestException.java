package com.extole.client.rest.logo;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class LogoValidationRestException extends ExtoleRestException {

    public static final ErrorCode<LogoValidationRestException> LOGO_CONTENT_LENGTH_EXCEEDED =
        new ErrorCode<>("logo_content_length_exceeded", 400, "Logo content exceed max size", "client_id",
            "max_allowed_file_size_bits");

    public static final ErrorCode<LogoValidationRestException> LOGO_CONTENT_EMPTY =
        new ErrorCode<>("logo_content_empty", 400, "Logo file is empty", "client_id");

    public static final ErrorCode<LogoValidationRestException> LOGO_FORMAT_NOT_SUPPORTED =
        new ErrorCode<>("logo_format_not_supported", 400, "Logo format not supported", "client_id",
            "format", "required_format");

    public static final ErrorCode<LogoValidationRestException> LOGO_ALREADY_EXISTS =
        new ErrorCode<>("logo_already_exists", 400, "Logo already exists", "client_id");

    public static final ErrorCode<LogoValidationRestException> LOGO_CONTENT_UPLOAD_ERROR =
        new ErrorCode<>("logo_content_upload_error", 400, "Logo content could not be uploaded", "client_id");

    public LogoValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
