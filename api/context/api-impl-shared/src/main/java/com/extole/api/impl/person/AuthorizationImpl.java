package com.extole.api.impl.person;

import java.util.Set;

import com.extole.api.person.Authorization;
import com.extole.common.lang.ToString;

public class AuthorizationImpl implements Authorization {
    private final String accessToken;
    private final String[] scopes;
    private final String clientId;
    private final String personId;

    public AuthorizationImpl(String accessToken, Set<String> scopes, String clientId, String personId) {
        this.accessToken = accessToken;
        this.scopes = scopes.toArray(new String[0]);
        this.clientId = clientId;
        this.personId = personId;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String[] getScopes() {
        return scopes;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getPersonId() {
        return personId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
