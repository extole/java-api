package com.extole.api.webhook;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.client.security.key.ClientKey;

@Schema
public interface Webhook {

    enum HttpMethod {
        GET, POST, PUT, PATCH
    }

    String getUrl();

    @Nullable
    ClientKey getClientKey();

    String getDefaultMethod();
}
