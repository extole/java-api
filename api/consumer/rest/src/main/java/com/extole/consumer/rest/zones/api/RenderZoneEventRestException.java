package com.extole.consumer.rest.zones.api;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RenderZoneEventRestException extends ExtoleRestException {

    public static final ErrorCode<RenderZoneEventRestException> MISSING_ZONE_NAME = new ErrorCode<>(
        "missing_zone_name", 400, "Zone name is required.");

    public static final ErrorCode<RenderZoneEventRestException> INVALID_ZONE_NAME = new ErrorCode<>(
        "invalid_zone_name", 400, "Zone name not valid.");

    public static final ErrorCode<RenderZoneEventRestException> INVALID_CREATIVE_RESULT = new ErrorCode<>(
        "invalid_creative_result", 400, "Configured creative did not return json.", "event_id", "content");

    public static final ErrorCode<RenderZoneEventRestException> UNEXPECTED_CONTENT_TYPE = new ErrorCode<>(
        "unexpected_content_type", 400, "Configured creative Content-Type not supported.", "contentType", "event_id",
        "accept_headers", "content");

    public static final ErrorCode<RenderZoneEventRestException> UNEXPECTED_REDIRECT = new ErrorCode<>(
        "unexpected_redirect", 400, "Creative redirect not supported.", "redirect_url");

    public static final ErrorCode<RenderZoneEventRestException> NO_CREATIVE = new ErrorCode<>(
        "no_creative", 400, "No Configured creative.");

    public RenderZoneEventRestException(String uniqueId, ErrorCode<RenderZoneEventRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
