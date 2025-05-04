package com.extole.consumer.rest.me.shareable.v4;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO remove ENG-10127
public class MeShareableV4RestException extends ExtoleRestException {

    public static final ErrorCode<MeShareableV4RestException> INVALID_SHAREABLE_ID =
        new ErrorCode<>("invalid_shareable_id", 403, "Invalid shareable id", "shareable_id");

    public MeShareableV4RestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
