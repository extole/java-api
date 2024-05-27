package com.extole.api.client.security.key;

import java.util.Map;

import javax.annotation.Nullable;

import com.extole.api.GlobalContext;

public interface OAuthClientKeyRuntimeContext extends GlobalContext {

    OAuthClientCredentials getClientCredentials();

    OAuthRequestBuilder createRequestBuilder();

    String encodeToFormUrlEncoded(Map<String, String> dataMap);

    @Nullable
    Object getVariable(String name);

    @Nullable
    Object getVariable(String name, String key);

    @Nullable
    Object getVariable(String name, String... keys);

}
