package com.extole.client.rest.me;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeResponse {

    private static final String USER_ID = "user_id";
    private static final String EMAIL = "email";
    private static final String CLIENT_ID = "client_id";
    private static final String PROPERTIES = "properties";
    private static final String INTERCOM_USER_HASH = "intercom_user_hash";

    private final String userId;
    private final String email;
    private final String clientId;
    private final Map<String, String> properties;
    private final String intercomUserHash;

    @JsonCreator
    public MeResponse(
        @JsonProperty(USER_ID) String userId,
        @JsonProperty(EMAIL) String email,
        @JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(PROPERTIES) Map<String, String> properties,
        @JsonProperty(INTERCOM_USER_HASH) String intercomUserHash) {
        this.userId = userId;
        this.email = email;
        this.clientId = clientId;
        this.properties = properties;
        this.intercomUserHash = intercomUserHash;
    }

    // When a superuser creates an access_token in the context of another client, the person associated with that token
    // does not have a user in that client, in that case the user_id will be null.
    @Nullable
    @JsonProperty(USER_ID)
    public String getUserId() {
        return userId;
    }

    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }

    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(PROPERTIES)
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonProperty(INTERCOM_USER_HASH)
    public String getIntercomUserHash() {
        return intercomUserHash;
    }

}
