package com.extole.client.rest.timeline.exception;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientTimelineEntryNotModifiableRestException extends ExtoleRestException {

    public static final ErrorCode<ClientTimelineEntryNotModifiableRestException> NOT_MODIFIABLE =
        new ErrorCode<>("not_modifiable", 400, "Entry cannot be modified");

    public ClientTimelineEntryNotModifiableRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
