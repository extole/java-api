package com.extole.client.rest.user;

public enum UserScope {
    /**
     * Functionally equivalent to {@link #USER_SUPPORT}; The UI (my.extole.com) uses this value to restrict
     * what a self service user sees on login.
     */
    SELF_SERVICE,
    USER_SUPPORT,
    CLIENT_ADMIN,
    CLIENT_SUPERUSER
}
