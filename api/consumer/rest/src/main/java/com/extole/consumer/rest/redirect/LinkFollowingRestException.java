package com.extole.consumer.rest.redirect;

import java.util.Map;

import javax.ws.rs.core.Response.Status;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class LinkFollowingRestException extends ExtoleRestException {

    public static final ErrorCode<LinkFollowingRestException> INVALID_PROGRAM =
        new ErrorCode<>("invalid_program", Status.BAD_REQUEST.getStatusCode(), "Invalid program.", "incomingUrl");

    public static final ErrorCode<LinkFollowingRestException> INVALID_ALIAS =
        new ErrorCode<>("invalid_alias", Status.BAD_REQUEST.getStatusCode(), "Invalid alias.", "incomingUrl");

    public static final ErrorCode<LinkFollowingRestException> INVALID_URI =
        new ErrorCode<>("invalid_uri", Status.BAD_REQUEST.getStatusCode(), "Invalid URI.", "incomingUrl");

    public static final ErrorCode<LinkFollowingRestException> PROGRAM_NOT_FOUND =
        new ErrorCode<>("program_not_found", Status.BAD_REQUEST.getStatusCode(), "Program not found for incoming url",
            "incomingUrl");

    public LinkFollowingRestException(String uniqueId, ErrorCode<LinkFollowingRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
