package com.extole.client.rest.security.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class OAuthFlowCodeExchangeRequest {

    private static final String CLIENT_KEY_ID = "client_key_id";
    private static final String KEY_TYPE = "key_type";
    private static final String CODE = "code";
    private static final String REDIRECT_URI = "redirect_uri";

    private final String clientKeyId;
    private final Omissible<OAuthKeyExchangeType> oAuthKeyExchangeType;
    private final String code;
    private final String redirectUri;

    public OAuthFlowCodeExchangeRequest(
        @JsonProperty(CLIENT_KEY_ID) String clientKeyId,
        @JsonProperty(KEY_TYPE) Omissible<OAuthKeyExchangeType> oAuthKeyExchangeType,
        @JsonProperty(CODE) String code,
        @JsonProperty(REDIRECT_URI) String redirectUri) {
        this.clientKeyId = clientKeyId;
        this.oAuthKeyExchangeType = oAuthKeyExchangeType;
        this.code = code;
        this.redirectUri = redirectUri;
    }

    @JsonProperty(CLIENT_KEY_ID)
    public String getClientKeyId() {
        return clientKeyId;
    }

    @JsonProperty(KEY_TYPE)
    public Omissible<OAuthKeyExchangeType> getOAuthKeyExchangeType() {
        return oAuthKeyExchangeType;
    }

    @JsonProperty(CODE)
    public String getCode() {
        return code;
    }

    @JsonProperty(REDIRECT_URI)
    public String getRedirectUri() {
        return redirectUri;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String clientKeyId;
        private OAuthKeyExchangeType oAuthKeyExchangeType;
        private String code;
        private String redirectUri;

        private Builder() {
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withClientKeyId(String clientKeyId) {
            this.clientKeyId = clientKeyId;
            return this;
        }

        public Builder withRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder withKeyType(OAuthKeyExchangeType keyType) {
            this.oAuthKeyExchangeType = keyType;
            return this;
        }

        public OAuthFlowCodeExchangeRequest build() {
            return new OAuthFlowCodeExchangeRequest(clientKeyId, Omissible.of(oAuthKeyExchangeType), code, redirectUri);
        }
    }
}
