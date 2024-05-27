package com.extole.api.client.security.key.response;

import javax.annotation.Nullable;

import com.extole.api.GlobalContext;

public interface OAuthClientKeyResponseContext extends GlobalContext {

    OAuthRequestResult getOAuthRequestResult();

    OAuthResponseBuilder createResponseBuilder();

    @Nullable
    Object getVariable(String variableName);

    @Nullable
    Object getVariable(String name, String key);

    @Nullable
    Object getVariable(String name, String... keys);
}
