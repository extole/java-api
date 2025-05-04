package com.extole.api.impl.event.webhook;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import com.extole.api.event.webhook.WebhookInputConsumerEventBuilder;
import com.extole.authorization.service.AuthorizationException;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.input.InputConsumerEventSendBuilder;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.consumer.event.service.processor.ProcessedRawEventProducer;
import com.extole.consumer.event.service.processor.exception.EventProcessorClientHasNoDomainException;
import com.extole.event.client.ClientEvent;
import com.extole.event.consumer.ApiType;
import com.extole.event.consumer.ClientDomainContext;
import com.extole.event.consumer.raw.HttpRequestMethod;
import com.extole.event.consumer.raw.RawEventProducer;
import com.extole.person.service.profile.Person;
import com.extole.security.backend.BackendAuthorization;

public class WebhookInputConsumerEventBuilderImpl implements WebhookInputConsumerEventBuilder {

    private final BackendAuthorization authorization;
    private final String webhookId;
    private final Person person;
    private final RawEventProducer rawEventProducer;
    private final ProcessedRawEventProducer processedRawEventProducer;
    private final ClientDomainContext clientDomainContext;
    private final ObjectMapper objectMapper;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final Map<String, Object> data = new HashMap<>();
    private String eventName;

    public WebhookInputConsumerEventBuilderImpl(
        BackendAuthorization authorization,
        String webhookId,
        Person person,
        RawEventProducer rawEventProducer,
        ProcessedRawEventProducer processedRawEventProducer,
        ClientDomainContext clientDomainContext,
        ObjectMapper objectMapper,
        ConsumerEventSenderService consumerEventSenderService) {
        this.authorization = authorization;
        this.webhookId = webhookId;
        this.person = person;
        this.rawEventProducer = rawEventProducer;
        this.processedRawEventProducer = processedRawEventProducer;
        this.clientDomainContext = clientDomainContext;
        this.objectMapper = objectMapper;
        this.consumerEventSenderService = consumerEventSenderService;
    }

    @Override
    public WebhookInputConsumerEventBuilder withEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    @Override
    public WebhookInputConsumerEventBuilder addData(String name, Object value) {
        data.put(name, value);
        return this;
    }

    @Override
    public void send() {
        try {
            InputConsumerEventSendBuilder eventBuilder =
                consumerEventSenderService.createInputEvent(authorization, buildProcessedRawEvent(), person);

            eventBuilder.addData(data).send();
        } catch (AuthorizationException | EventProcessorClientHasNoDomainException e) {
            throw new RuntimeException("Failed to send the input event", e);
        }
    }

    private ProcessedRawEvent buildProcessedRawEvent() throws EventProcessorClientHasNoDomainException {
        Preconditions.checkNotNull(eventName);

        Multimap<String, String> httpHeaders = ArrayListMultimap.create();
        httpHeaders.put("Content-Type", "application/json");

        Map<String, String> appData = new HashMap<>();
        appData.put("webhook_id", webhookId);
        RawEventProducer.RawEventBuilder rawEventBuilder = rawEventProducer
            .createBuilder()
            .withApiType(ApiType.WEBHOOK_DISPATCHER)
            .withAppData(appData)
            .withEventName(eventName)
            .withUrl(ClientEvent.WEBHOOK_PREFIX + webhookId)
            .withHttpHeaders(httpHeaders)
            .withClientDomainContext(clientDomainContext)
            .withHttpRequestMethod(HttpRequestMethod.POST);

        if (!data.isEmpty()) {
            try {
                rawEventBuilder.withHttpRequestBody(objectMapper.writeValueAsString(data));
            } catch (JsonProcessingException e) {
                rawEventBuilder.withHttpRequestBody("unknown");
            }
        }

        return processedRawEventProducer.createBuilder(rawEventBuilder.build()).build();
    }

}
