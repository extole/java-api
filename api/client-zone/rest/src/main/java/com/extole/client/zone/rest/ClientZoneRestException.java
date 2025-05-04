package com.extole.client.zone.rest;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientZoneRestException extends ExtoleRestException {

    public static final ErrorCode<ClientZoneRestException> MISSING_ZONE_NAME =
        new ErrorCode<>("missing_zone_name", 400, "Missing zone name");

    public static final ErrorCode<ClientZoneRestException> INVALID_TIME_FORMAT =
        new ErrorCode<>("invalid_time_format", 400,
            "Invalid time format. Expected: ISO8601 format", "time");

    public static final ErrorCode<ClientZoneRestException> INVALID_TIME_ZONE =
        new ErrorCode<>("invalid_time_zone", 400, "Invalid time zone.", "time_zone");

    public static final ErrorCode<ClientZoneRestException> INVALID_REDIRECT =
        new ErrorCode<>("invalid_redirect", 400, "Invalid redirect", "invalid_redirect_urls");

    public static final ErrorCode<ClientZoneRestException> NO_CREATIVE =
        new ErrorCode<>("no_creative", 400, "No Configured creative.");

    public ClientZoneRestException(String uniqueId, ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
