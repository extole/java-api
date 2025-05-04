package com.extole.client.rest.client;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ResourceAccessTokenRestException extends ExtoleRestException {

    public static final ErrorCode<ResourceAccessTokenRestException> NO_SUCH_RESOURCE_TOKEN =
        new ErrorCode<>("no_such_resource_token", 400,
            "The access_token provided is could not be found.");

    public ResourceAccessTokenRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
