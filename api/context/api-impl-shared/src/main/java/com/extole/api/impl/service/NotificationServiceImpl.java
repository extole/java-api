package com.extole.api.impl.service;

import com.extole.api.service.NotificationBuilder;
import com.extole.api.service.NotificationService;
import com.extole.authorization.service.ClientHandle;
import com.extole.event.client.ClientEventService;
import com.extole.id.Id;

public class NotificationServiceImpl implements NotificationService {

    private final Id<ClientHandle> clientId;
    private final ClientEventService clientEventService;
    private final String idType;
    private final Id<?> contextObjectId;

    public NotificationServiceImpl(Id<ClientHandle> clientId,
        ClientEventService clientEventService, String idType, Id<?> contextObjectId) {
        this.clientId = clientId;
        this.clientEventService = clientEventService;
        this.idType = idType;
        this.contextObjectId = contextObjectId;
    }

    @Override
    public NotificationBuilder createNotification() {
        return createNotificationBuilder(clientId);
    }

    @Deprecated // TODO remove as notificationKey is unused ENG-14958
    @Override
    public NotificationBuilder createNotification(String notificationKey) {
        return createNotificationBuilder(clientId);
    }

    private NotificationBuilderImpl createNotificationBuilder(Id<ClientHandle> clientId) {
        return new NotificationBuilderImpl(clientEventService, clientId, idType, contextObjectId);
    }

}
