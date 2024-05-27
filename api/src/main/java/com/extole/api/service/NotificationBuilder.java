package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface NotificationBuilder {

    NotificationBuilder withName(String name);

    NotificationBuilder withMessage(String message);

    NotificationBuilder withLevel(String level);

    NotificationBuilder withScope(String scope);

    NotificationBuilder addTag(String tag);

    NotificationBuilder addParameter(String name, String value);

    void send();
}
