package com.extole.client.topic.rest.impl.notification;

import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.topic.event.service.NotificationRecentEvent;
import com.extole.client.topic.rest.ChannelType;
import com.extole.client.topic.rest.Level;
import com.extole.client.topic.rest.NotificationCursorResponse;
import com.extole.client.topic.rest.NotificationResponse;

@Component
public class NotificationRestMapper {

    public NotificationResponse toNotificationResponse(NotificationRecentEvent event, ZoneId timeZone) {
        return new NotificationResponse(
            event.getEventId().getValue(),
            event.getClientId().getValue(),
            event.getClientVersion(),
            event.getEventTime().atZone(timeZone),
            event.getName(),
            event.getTags(),
            event.getMessage(),
            event.getData(),
            Level.valueOf(event.getLevel().name()),
            event.getSnoozeId().map(id -> id.getValue()),
            event.getUserId().getValue(),
            event.getChannels().stream()
                .map(channelType -> ChannelType.valueOf(channelType.name())).collect(Collectors.toList()),
            event.getCauseEventId().getValue(),
            event.getCauseUserId().map(id -> id.getValue()),
            event.getSubscriptionId().getValue(),
            event.getSubscriptionDedupeDurationMs());
    }

    public NotificationCursorResponse toNotificationCursorResponse(Instant cursor, ZoneId timeZone) {
        return new NotificationCursorResponse(cursor.atZone(timeZone));
    }

}
