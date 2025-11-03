package com.extole.client.identity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = IdentityKeyImpl.class)
public interface IdentityKey {

    IdentityKey EMAIL_IDENTITY_KEY = IdentityKey.valueOf("email");

    String getName();

    static IdentityKey valueOf(String name) {
        return IdentityKeyImpl.valueOf(name);
    }
}
