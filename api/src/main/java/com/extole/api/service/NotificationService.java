package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface NotificationService {

    NotificationBuilder createNotification();

    @Deprecated // TODO remove as notificationKey is unused ENG-14958
    NotificationBuilder createNotification(String notificationKey);
}
