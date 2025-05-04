package com.extole.api.impl.event.internal;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.extole.api.event.internal.InternalConsumerEvent;
import com.extole.api.impl.event.ConsumerEventImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public class InternalConsumerEventImpl extends ConsumerEventImpl implements InternalConsumerEvent {

    private final String name;
    private final List<String> labels;
    private final String campaignId;

    protected InternalConsumerEventImpl(com.extole.event.consumer.internal.InternalConsumerEvent event, Person person) {
        super(event, person);
        this.name = event.getName();
        this.labels = ImmutableList.copyOf(event.getLabels().stream()
            .map(value -> value.getName())
            .collect(Collectors.toList()));
        this.campaignId = event.getCampaignId().getValue();
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
    public String getCampaignId() {
        return campaignId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static InternalConsumerEvent newInstance(com.extole.event.consumer.internal.InternalConsumerEvent event,
        Person person) {
        return new InternalConsumerEventImpl(event, person);
    }

}
