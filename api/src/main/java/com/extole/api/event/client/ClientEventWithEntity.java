package com.extole.api.event.client;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.model.EventEntity;

@Schema
public interface ClientEventWithEntity extends ClientEvent {

    String getEntityType();

    EventEntity getEntity();
}
