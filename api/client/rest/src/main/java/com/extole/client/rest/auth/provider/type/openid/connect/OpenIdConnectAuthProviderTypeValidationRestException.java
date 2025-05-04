package com.extole.client.rest.auth.provider.type.openid.connect;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OpenIdConnectAuthProviderTypeValidationRestException extends ExtoleRestException {

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_MISSING_DOMAIN =
            new ErrorCode<>(
                "oidc_auth_provider_type_missing_domain", 400, "Auth Provider type required domain is not specified");

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_MISSING_APP_ID =
            new ErrorCode<>(
                "oidc_auth_provider_type_missing_application_id", 400,
                "Auth Provider type required application id is not specified");

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_MISSING_APP_SECRET =
            new ErrorCode<>(
                "oidc_auth_provider_type_missing_application_secret", 400,
                "Auth Provider type required application secret is not specified");

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_MISSING_CATEGORY =
            new ErrorCode<>(
                "oidc_auth_provider_type_missing_category", 400,
                "Auth Provider type required category is not specified");

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_INVALID_DOMAIN =
            new ErrorCode<>(
                "oidc_auth_provider_type_invalid_domain", 400,
                "Domain should be secure (https) and be up to 255 containing ASCII characters", "domain");

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_INVALID_APP_ID =
            new ErrorCode<>(
                "oidc_auth_provider_type_invalid_application_id", 400,
                "Allowed application id length is 255 containing ASCII characters", "application_id");

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_INVALID_APP_SECRET =
            new ErrorCode<>(
                "oidc_auth_provider_type_invalid_application_secret", 400,
                "Allowed application secret should be up to 4096 characters");

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_TOO_MANY_CUSTOM_PARAMS =
            new ErrorCode<>(
                "oidc_auth_provider_type_too_many_custom_params", 400,
                "Too many custom params", "max_custom_params");

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_INVALID_CUSTOM_PARAM =
            new ErrorCode<>(
                "oidc_auth_provider_type_invalid_custom_param", 400,
                "Custom param key/value should be alpha num, without commas or equality signs", "key", "value");

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_CUSTOM_PARAMS_TOTAL_LENGTH_TOO_LONG =
            new ErrorCode<>(
                "oidc_auth_provider_type_custom_params_total_length_too_long", 400,
                "The total length of custom URL params is too long, remove some or try shorter values");

    public static final ErrorCode<
        OpenIdConnectAuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_APP_SECRET_NOT_BASE64 =
            new ErrorCode<>(
                "oidc_auth_provider_type_application_secret_not_base64_encoded", 400,
                "Auth Provider type application secret is expected to be Base64 encoded");

    public OpenIdConnectAuthProviderTypeValidationRestException(String uniqueId,
        ErrorCode<OpenIdConnectAuthProviderTypeValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
