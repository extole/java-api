package com.extole.client.rest.client.core;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientCoreAssetsVersionRestException extends ExtoleRestException {

    public static final ErrorCode<ClientCoreAssetsVersionRestException> CORE_ASSETS_VERSION_NOT_FOUND =
        new ErrorCode<>("core_assets_version_not_found", 400, "Core assets version not found", "core_assets_version");

    public ClientCoreAssetsVersionRestException(String uniqueId, ErrorCode<ClientCoreAssetsVersionRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
