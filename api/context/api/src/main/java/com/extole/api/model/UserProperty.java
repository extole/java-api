package com.extole.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface UserProperty extends EventEntity {
    String getId();

    String getName();

    String getValue();

    String getUserId();
}
