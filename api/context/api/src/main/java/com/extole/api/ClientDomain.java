package com.extole.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ClientDomain {

    String getId();

    String getName();

    String getDomain();

    String getShareUri();

    String getScheme();
}
