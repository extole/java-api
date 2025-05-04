package com.extole.client.rest.core;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientCoreSettingsRestException extends ExtoleRestException {
    public static final ErrorCode<ClientCoreSettingsRestException> INVALID_CORE_VERSION =
        new ErrorCode<>("invalid_core_version", 400, "Invalid core version", "core_version");

    public static final ErrorCode<ClientCoreSettingsRestException> INVALID_EXTENDED_CORE_JAVASCRIPT =
        new ErrorCode<>("invalid_extended_core_javascript", 400, "Invalid extended core javascript", "output");

    public static final ErrorCode<ClientCoreSettingsRestException> ILLEGAL_STATE_FOR_CREATIVE_RESPONDS_HTML_ENABLED =
        new ErrorCode<>("creative_responds_html_enabled_illegal_state", 400,
            "Cannot set creative_responds_html_enabled=true when zone_post_enabled=false");

    public ClientCoreSettingsRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
