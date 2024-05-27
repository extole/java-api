package com.extole.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface PublicClientDomain {
    String getClientDomain();

    String getClientDomainId();
}
