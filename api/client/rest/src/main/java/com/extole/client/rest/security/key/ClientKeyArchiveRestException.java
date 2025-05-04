package com.extole.client.rest.security.key;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientKeyArchiveRestException extends ExtoleRestException {

    public static final ErrorCode<ClientKeyArchiveRestException> CLIENT_KEY_ASSOCIATED_WITH_ENTITY =
        new ErrorCode<>("client_key_associated_with_entity", 400,
            "Can't delete a client key associated with an entity", "key_id", "entity_type", "entity_ids");

    public ClientKeyArchiveRestException(String uniqueId, ErrorCode<ClientKeyArchiveRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
