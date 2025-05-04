package com.extole.client.rest.client;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ManagedAccessTokenResponse {

    private static final String JSON_ACCESS_TOKEN_ID = "access_token_id";
    private static final String JSON_EXPIRES_IN = "expires_in";
    private static final String JSON_EXPIRATION_DATE = "expire_date";
    private static final String JSON_CLIENT_ID = "client_id";
    private static final String JSON_NAME = "name";
    private static final String JSON_CREATED_AT = "created_at";
    private static final String JSON_CREATION_DATE = "create_date";
    private static final String JSON_ACCESS_TOKEN = "access_token";
    private static final String JSON_ACCESS_TOKEN_RESPONSE = "access_token_response";

    private final String clientId;
    private final String name;
    private final ZonedDateTime expireDate;
    private final ZonedDateTime createDate;
    private final String accessTokenId;
    private final String accessToken;
    private final Optional<AccessTokenResponse> accessTokenResponse;

    ManagedAccessTokenResponse(@JsonProperty(JSON_ACCESS_TOKEN_ID) String accessTokenId,
        @JsonProperty(JSON_CLIENT_ID) String clientId,
        @JsonProperty(JSON_EXPIRATION_DATE) ZonedDateTime expireDate,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_CREATION_DATE) ZonedDateTime createDate,
        @JsonProperty(JSON_ACCESS_TOKEN) String accessToken,
        @JsonProperty(JSON_ACCESS_TOKEN_RESPONSE) Optional<AccessTokenResponse> accessTokenResponse) {
        this.accessTokenId = accessTokenId;
        this.clientId = clientId;
        this.expireDate = expireDate;
        this.name = name;
        this.createDate = createDate;
        this.accessToken = accessToken;
        this.accessTokenResponse = accessTokenResponse;
    }

    @JsonProperty(JSON_ACCESS_TOKEN_ID)
    public String getAccessTokenId() {
        return accessTokenId;
    }

    @Deprecated // TODO remove in ENG-15541
    @JsonProperty(JSON_EXPIRES_IN)
    public long getExpiresIn() {
        return expireDate.toEpochSecond();
    }

    @JsonProperty(JSON_EXPIRATION_DATE)
    public ZonedDateTime getExpireDate() {
        return expireDate;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @Deprecated // TODO remove in ENG-15541
    @JsonProperty(JSON_CREATED_AT)
    public long getCreatedAt() {
        return createDate.toEpochSecond();
    }

    @JsonProperty(JSON_CREATION_DATE)
    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    @JsonProperty(JSON_ACCESS_TOKEN)
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty(JSON_ACCESS_TOKEN_RESPONSE)
    public Optional<AccessTokenResponse> getAccessTokenResponse() {
        return accessTokenResponse;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String clientId;
        private String name;
        private ZonedDateTime expireDate;
        private ZonedDateTime createDate;
        private String accessTokenId;
        private String accessToken;
        private Optional<AccessTokenResponse> accessTokenResponse = Optional.empty();

        private Builder() {
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withExpireDate(ZonedDateTime expireDate) {
            this.expireDate = expireDate;
            return this;
        }

        public Builder withCreateDate(ZonedDateTime createDate) {
            this.createDate = createDate;
            return this;
        }

        public Builder withAccessTokenId(String accessTokenId) {
            this.accessTokenId = accessTokenId;
            return this;
        }

        public Builder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder withAccessTokenResponse(Optional<AccessTokenResponse> accessTokenResponse) {
            this.accessTokenResponse = accessTokenResponse;
            return this;
        }

        public ManagedAccessTokenResponse build() {
            return new ManagedAccessTokenResponse(accessTokenId, clientId, expireDate, name, createDate, accessToken,
                accessTokenResponse);
        }
    }
}
