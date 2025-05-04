package com.extole.api.impl.event.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import com.extole.api.event.internal.InternalConsumerEvent;
import com.extole.api.event.internal.InternalConsumerEventBuilder;
import com.extole.api.impl.event.ConsumerEventToApiEventMapper;
import com.extole.api.impl.person.PersonImpl;
import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.internal.InternalConsumerEventSendBuilder;
import com.extole.event.consumer.ClientContext;
import com.extole.event.consumer.ClientDomainContext;
import com.extole.event.consumer.ConsumerEventJourneyContext;
import com.extole.event.consumer.ConsumerEventLabel;
import com.extole.event.consumer.ConsumerEventLabelPriority;
import com.extole.id.Id;
import com.extole.model.entity.client.PublicClient;
import com.extole.person.service.CampaignHandle;
import com.extole.person.service.ProgramHandle;
import com.extole.person.service.profile.Person;
import com.extole.sandbox.Sandbox;
import com.extole.sandbox.SandboxService;

public class InternalConsumerEventBuilderImpl implements InternalConsumerEventBuilder {

    private final SandboxService sandboxService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final Person person;
    private final PublicClient client;
    private final Id<CampaignHandle> defaultCampaignId;
    private final List<ConsumerEventLabel> defaultLabels;
    private final String clientDomain;
    private final Id<ProgramHandle> clientDomainId;
    private final Id<?> causeEventId;
    private final Id<?> rootEventId;
    private final Integer causeEventSequence;
    private final Map<String, Object> data = new HashMap<>();
    private final List<String> labels = new ArrayList<>();
    private final Sandbox sandbox;
    private final Optional<ConsumerEventJourneyContext> journeyContext;

    private String name;
    private Optional<String> campaignId = Optional.empty();

    public InternalConsumerEventBuilderImpl(
        SandboxService sandboxService,
        ConsumerEventSenderService consumerEventSenderService,
        PublicClient client,
        Person person,
        Id<CampaignHandle> defaultCampaignId,
        List<ConsumerEventLabel> defaultLabels,
        String clientDomain,
        Id<ProgramHandle> clientDomainId,
        Id<?> causeEventId,
        Id<?> rootEventId,
        Integer causeEventSequence,
        Sandbox sandbox,
        Optional<ConsumerEventJourneyContext> journeyContext) {
        this.sandboxService = sandboxService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.client = client;
        this.person = person;
        this.defaultCampaignId = defaultCampaignId;
        this.journeyContext = journeyContext;
        this.defaultLabels = ImmutableList.copyOf(defaultLabels);
        this.clientDomain = clientDomain;
        this.clientDomainId = clientDomainId;
        this.causeEventId = causeEventId;
        this.rootEventId = rootEventId;
        this.causeEventSequence = causeEventSequence;
        this.sandbox = sandbox;
    }

    @Override
    public InternalConsumerEventBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public InternalConsumerEventBuilder addData(String name, Object value) {
        data.put(name, value);
        return this;
    }

    @Override
    public InternalConsumerEventBuilder withCampaignId(String campaignId) {
        this.campaignId = Optional.ofNullable(campaignId);
        return this;
    }

    @Override
    public InternalConsumerEventBuilder addLabel(String label) {
        if (StringUtils.isNotBlank(label)) {
            labels.add(label);
        }
        return this;
    }

    @Override
    public InternalConsumerEvent send() {
        Preconditions.checkNotNull(name);

        ClientContext clientContext =
            new ClientContext(client.getId(), client.getShortName(), client.getTimeZone(), client.getVersion());
        InternalConsumerEventSendBuilder eventBuilder = consumerEventSenderService.createInternalEvent(person, name)
            .withData(data)
            .withClientContext(clientContext)
            .withClientDomainContext(new ClientDomainContext(clientDomain, clientDomainId))
            .withCauseEventId(causeEventId)
            .withRootEventId(rootEventId)
            .withCauseEventSequence(causeEventSequence)
            .withSandbox(sandbox);

        if (campaignId.isPresent()) {
            eventBuilder.withCampaignId(Id.valueOf(campaignId.get()));
        } else {
            eventBuilder.withCampaignId(defaultCampaignId);
        }

        if (!labels.isEmpty()) {
            List<ConsumerEventLabel> consumerEventLabels = labels.stream()
                .map(value -> new ConsumerEventLabel(value, true, ConsumerEventLabelPriority.NORMAL))
                .collect(Collectors.toList());
            eventBuilder.withLabels(consumerEventLabels);
        } else {
            eventBuilder.withLabels(defaultLabels);
        }

        journeyContext.ifPresent(value -> eventBuilder.withJourneyContext(value));

        com.extole.event.consumer.internal.InternalConsumerEvent internalConsumerEvent = eventBuilder.send();

        return new ConsumerEventToApiEventMapper().mapInternalConsumerEvent(internalConsumerEvent,
            new LazyLoadingSupplier<>(() -> new PersonImpl(person, sandboxService)));
    }

}
