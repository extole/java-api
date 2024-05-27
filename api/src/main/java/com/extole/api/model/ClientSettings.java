package com.extole.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ClientSettings extends EventEntity {
    String getId();

    String getTimezone();
}
