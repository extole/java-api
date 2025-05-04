package com.extole.consumer.rest.debug;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ContainerRestException extends ExtoleRestException {

    public static final ErrorCode<ContainerRestException> INVALID_CONTAINER =
        new ErrorCode<>("invalid_container", 403, "Container only allows alphanumeric or - characters.");

    public ContainerRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
