package com.extole.api.impl.event.internal.message;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import com.extole.api.event.ReferralContext;
import com.extole.api.event.internal.message.MessageConsumerEvent;
import com.extole.api.impl.event.ConsumerEventImpl;
import com.extole.api.impl.event.ReferralContextImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public final class MessageConsumerEventImpl extends ConsumerEventImpl implements MessageConsumerEvent {

    private final String name;
    private final List<String> labels;
    private final Optional<String> campaignId;
    private final String messageEventType;
    private final Optional<ReferralContext> referralContext;
    private final String messageId;

    private MessageConsumerEventImpl(com.extole.event.consumer.internal.message.MessageConsumerEvent event,
        Person person) {
        super(event, person);
        this.name = event.getName();
        this.labels = ImmutableList.copyOf(event.getLabels().stream()
            .map(value -> value.getName())
            .collect(Collectors.toList()));
        this.campaignId = event.getCampaignId().map(value -> value.getValue());
        this.messageEventType = event.getMessageEventType();
        this.referralContext = createReferralContext(event);
        this.messageId = event.getMessageId() != null ? event.getMessageId().getValue() : null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getLabels() {
        return labels.toArray(new String[] {});
    }

    @Override
    @Nullable
    public String getCampaignId() {
        return campaignId.orElse(null);
    }

    @Override
    public String getMessageEventType() {
        return messageEventType;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static MessageConsumerEvent newInstance(
        com.extole.event.consumer.internal.message.MessageConsumerEvent event, Person person) {
        return new MessageConsumerEventImpl(event, person);
    }

    @Nullable
    @Override
    public ReferralContext getReferralContext() {
        return referralContext.orElse(null);
    }

    private static Optional<ReferralContext>
        createReferralContext(com.extole.event.consumer.internal.message.MessageConsumerEvent event) {
        return event.getReferralContext().map(context -> new ReferralContextImpl(context));
    }

}
