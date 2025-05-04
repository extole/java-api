package com.extole.client.rest.logo;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class LogoRestException extends ExtoleRestException {

    public static final ErrorCode<LogoRestException> LOGO_NOT_FOUND =
        new ErrorCode<>("logo_not_found", 400, "Logo not found", "client_id");

    public static final ErrorCode<LogoRestException> LOGO_CONTENT_DOWNLOAD_ERROR =
        new ErrorCode<>("logo_content_download_error", 400, "Logo content could not be downloaded", "client_id");

    public LogoRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
