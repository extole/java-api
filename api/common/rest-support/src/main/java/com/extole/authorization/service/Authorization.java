package com.extole.authorization.service;

import java.util.Collection;

public interface Authorization {
    String getAccessToken();
    Collection<Scope> getScopes();
    String getClientId();
    String getPersonId();

    enum Scope {
        CLIENT_SUPERUSER
    }
}

