package com.extole.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Client extends com.extole.api.client.Client,
    EventEntity {
    String getCreatedDate();

    String getUpdatedDate();
}
