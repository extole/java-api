package com.extole.client.rest.timeline.exception;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientTimelineEntryNotFoundRestException extends ExtoleRestException {

    public static final ErrorCode<ClientTimelineEntryNotFoundRestException> ENTRY_NOT_FOUND =
        new ErrorCode<>("timeline_entry_not_found", 400, "Timeline entry not found");

    public ClientTimelineEntryNotFoundRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
