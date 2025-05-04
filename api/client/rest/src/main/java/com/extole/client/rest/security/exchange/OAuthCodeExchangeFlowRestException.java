package com.extole.client.rest.security.exchange;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OAuthCodeExchangeFlowRestException extends ExtoleRestException {
    public static final ErrorCode<OAuthCodeExchangeFlowRestException> EXCHANGE_CLIENT_KEY_NOT_FOUND =
        new ErrorCode<>("exchange_client_key_not_found", 403, "Exchange ClientKey not found",
            "client_key_id");

    public static final ErrorCode<OAuthCodeExchangeFlowRestException> CLIENT_KEY_EXCHANGER_NOT_FOUND =
        new ErrorCode<>("client_key_exchanger_not_found", 403,
            "Failed to exchange code to an access token, client key exchanger not found",
            "client_key_exchanger_algorithm");

    public static final ErrorCode<OAuthCodeExchangeFlowRestException> KEY_EXCHANGE_EXCEPTION =
        new ErrorCode<>("key_exchange_exception", 403,
            "Failed to exchange code", "details");

    public static final ErrorCode<OAuthCodeExchangeFlowRestException> EXCHANGED_CLIENT_KEY_CREATION_FAILED =
        new ErrorCode<>("exchanged_client_key_creation_failed", 403,
            "Failed to create a client key using exchanged access token");

    public static final ErrorCode<OAuthCodeExchangeFlowRestException> INVALID_KEY_EXCHANGE_RESPONSE =
        new ErrorCode<>("invalid_key_exchange_response", 403,
            "Received invalid response when exchange code to access token");

    public static final ErrorCode<OAuthCodeExchangeFlowRestException> CLIENT_KEY_ALREADY_EXISTS =
        new ErrorCode<>("client_key_already_exists", 403,
            "Client key for this integration already exists", "client_key_id");

    public OAuthCodeExchangeFlowRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
