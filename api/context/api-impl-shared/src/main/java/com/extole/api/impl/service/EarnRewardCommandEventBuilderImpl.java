package com.extole.api.impl.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.extole.api.service.EarnRewardCommandEventBuilder;
import com.extole.event.reward.command.earn.EarnRewardCommandEventProducer;
import com.extole.id.Id;
import com.extole.person.service.profile.journey.JourneyName;

public class EarnRewardCommandEventBuilderImpl implements EarnRewardCommandEventBuilder {

    // TODO Remove ENG-18714
    private static final String DATA_NAME_REWARD_JOURNEY_NAME = "journey_name";
    private static final String EARNED_EVENT_VALUE = "earned_event_value";

    private final EarnRewardCommandEventProducer.EarnRewardCommandEventBuilder eventBuilder;
    private final Map<String, String> data;
    private final Set<String> tags;

    public EarnRewardCommandEventBuilderImpl(
        EarnRewardCommandEventProducer.EarnRewardCommandEventBuilder eventBuilder, JourneyName journeyName) {
        this.eventBuilder = eventBuilder;
        this.data = new HashMap<>();
        this.data.put(DATA_NAME_REWARD_JOURNEY_NAME, journeyName.getValue());

        this.tags = new HashSet<>();
    }

    @Override
    public EarnRewardCommandEventBuilder withRewardName(String name) {
        eventBuilder.withRewardName(name);
        return this;
    }

    @Override
    public EarnRewardCommandEventBuilder addData(String key, String value) {
        this.data.put(key, value);
        return this;
    }

    @Override
    public EarnRewardCommandEventBuilder withRewardSupplierId(String rewardSupplierId) {
        eventBuilder.withRewardSupplierId(Id.valueOf(rewardSupplierId));
        return this;
    }

    @Override
    public EarnRewardCommandEventBuilder withEarnedEventValue(String earnedEventValue) {
        this.data.put(EARNED_EVENT_VALUE, earnedEventValue);
        return this;
    }

    @Override
    public EarnRewardCommandEventBuilder addTag(String tag) {
        this.tags.add(tag);
        return this;
    }

    @Override
    public void send() {
        eventBuilder.withData(data);
        eventBuilder.withTags(tags);
        this.eventBuilder.send();
    }
}
