package com.extole.client.rest.timeline.exception;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientTimelineEntryRestException extends ExtoleRestException {

    public static final ErrorCode<ClientTimelineEntryRestException> NAME_MISSING =
        new ErrorCode<>("name_missing", 400, "Name is missing");

    public static final ErrorCode<ClientTimelineEntryRestException> NAME_INVALID =
        new ErrorCode<>("name_invalid", 400, "Name is of invalid format or length");

    public static final ErrorCode<ClientTimelineEntryRestException> NAME_EXISTS =
        new ErrorCode<>("entry_name_exists", 400, "Entry for given name already exists");

    public static final ErrorCode<ClientTimelineEntryRestException> DESCRIPTION_INVALID =
        new ErrorCode<>("description_invalid", 400, "Description is of invalid format or length");

    public static final ErrorCode<ClientTimelineEntryRestException> DATE_MISSING =
        new ErrorCode<>("date_missing", 400, "Date is missing");

    public static final ErrorCode<ClientTimelineEntryRestException> DATE_INVALID = new ErrorCode<>(
        "date_invalid", 400, "Date is invalid or not ISO-8601 compliant");

    public ClientTimelineEntryRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
