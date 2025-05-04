package com.extole.client.rest.shareable.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO remove client-shareable-v2 ENG-10128
public class ClientShareableLookupV2RestException extends ExtoleRestException {
    public static final ErrorCode<ClientShareableLookupV2RestException> CONSTRAINT_REQUIRED =
        new ErrorCode<>("constraint_required", 403, "Query for shareables requires a constraint, e.g code");

    public ClientShareableLookupV2RestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
