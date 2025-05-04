package com.extole.webhook.dispatcher.rest.impl.dispatch;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import com.extole.common.lang.ObjectMapperProvider;
import com.extole.webhook.dispatcher.rest.dispatch.response.ClientWebhookDispatchResponse;
import com.extole.webhook.dispatcher.rest.dispatch.response.ConsumerWebhookDispatchResponse;
import com.extole.webhook.dispatcher.rest.dispatch.response.PartnerWebhookDispatchResponse;
import com.extole.webhook.dispatcher.rest.dispatch.response.RewardWebhookDispatchResponse;
import com.extole.webhook.dispatcher.rest.dispatch.response.WebhookDispatchResponse;
import com.extole.webhook.event.service.event.ClientWebhookRecentEvent;
import com.extole.webhook.event.service.event.ConsumerWebhookRecentEvent;
import com.extole.webhook.event.service.event.PartnerWebhookRecentEvent;
import com.extole.webhook.event.service.event.RewardWebhookRecentEvent;
import com.extole.webhook.event.service.event.WebhookRecentEvent;

@Component
public class WebhookDispatchRestMapper {

    public WebhookDispatchResponse toWebhookDispatchResponse(WebhookRecentEvent webhookRecentEvent, ZoneId timeZone) {
        if (webhookRecentEvent.getClass() == ConsumerWebhookRecentEvent.class) {
            return toConsumerWebhookDispatchResponse((ConsumerWebhookRecentEvent) webhookRecentEvent, timeZone);
        }
        if (webhookRecentEvent.getClass() == ClientWebhookRecentEvent.class) {
            return toClientWebhookDispatchResponse((ClientWebhookRecentEvent) webhookRecentEvent, timeZone);
        }
        if (webhookRecentEvent.getClass() == RewardWebhookRecentEvent.class) {
            return toRewardWebhookDispatchResponse((RewardWebhookRecentEvent) webhookRecentEvent, timeZone);
        }
        if (webhookRecentEvent.getClass() == PartnerWebhookRecentEvent.class) {
            return toPartnerWebhookDispatchResponse((PartnerWebhookRecentEvent) webhookRecentEvent, timeZone);
        }

        throw new IllegalStateException("No possible to map type " + webhookRecentEvent.getClass().getSimpleName()
            + " to specific response");
    }

    private ConsumerWebhookDispatchResponse
        toConsumerWebhookDispatchResponse(ConsumerWebhookRecentEvent webhookRecentConsumerEvent, ZoneId timeZone) {
        return new ConsumerWebhookDispatchResponse(
            webhookRecentConsumerEvent.getEventId().getValue(),
            webhookRecentConsumerEvent.getClientId().getValue(),
            webhookRecentConsumerEvent.getWebhookId().getValue(),
            webhookRecentConsumerEvent.getEventTime().atZone(timeZone),
            webhookRecentConsumerEvent.getData());
    }

    private ClientWebhookDispatchResponse toClientWebhookDispatchResponse(
        ClientWebhookRecentEvent webhookRecentClientEvent,
        ZoneId timeZone) {
        Map<String, Object> data = new HashMap<>(webhookRecentClientEvent.getData());
        Map<String, Object> clientEvent = new HashMap<>((Map<String, Object>) data.get("client_event"));

        ZonedDateTime clientEventZonedDateTime = ObjectMapperProvider.getConfiguredInstance()
            .convertValue(clientEvent.get("event_time"),
                new TypeReference<Instant>() {})
            .atZone(timeZone);

        clientEvent.put("event_time", clientEventZonedDateTime);
        data.put("client_event", clientEvent);

        return new ClientWebhookDispatchResponse(
            webhookRecentClientEvent.getEventId().getValue(),
            webhookRecentClientEvent.getClientId().getValue(),
            webhookRecentClientEvent.getWebhookId().getValue(),
            webhookRecentClientEvent.getEventTime().atZone(timeZone),
            ImmutableMap.copyOf(data));
    }

    private RewardWebhookDispatchResponse
        toRewardWebhookDispatchResponse(RewardWebhookRecentEvent webhookRecentRewardEvent, ZoneId timeZone) {
        return new RewardWebhookDispatchResponse(
            webhookRecentRewardEvent.getEventId().getValue(),
            webhookRecentRewardEvent.getClientId().getValue(),
            webhookRecentRewardEvent.getWebhookId().getValue(),
            webhookRecentRewardEvent.getEventTime().atZone(timeZone),
            webhookRecentRewardEvent.getData());
    }

    private PartnerWebhookDispatchResponse
        toPartnerWebhookDispatchResponse(PartnerWebhookRecentEvent webhookRecentPartnerEvent, ZoneId timeZone) {
        return new PartnerWebhookDispatchResponse(
            webhookRecentPartnerEvent.getEventId().getValue(),
            webhookRecentPartnerEvent.getClientId().getValue(),
            webhookRecentPartnerEvent.getWebhookId().getValue(),
            webhookRecentPartnerEvent.getEventTime().atZone(timeZone),
            webhookRecentPartnerEvent.getData());
    }

}
