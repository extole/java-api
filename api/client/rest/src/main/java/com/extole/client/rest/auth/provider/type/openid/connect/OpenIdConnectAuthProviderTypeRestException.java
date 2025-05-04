package com.extole.client.rest.auth.provider.type.openid.connect;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OpenIdConnectAuthProviderTypeRestException extends ExtoleRestException {

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeRestException> OPEN_ID_CONNECT_AUTH_PROVIDER_TYPE_NOT_FOUND =
            new ErrorCode<>(
                "openid_connect_auth_provider_type_not_found", 400, "OpenID Connect Auth provider type is not found",
                "oidc_auth_provider_type_id");

    public OpenIdConnectAuthProviderTypeRestException(String uniqueId,
        ErrorCode<OpenIdConnectAuthProviderTypeRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
