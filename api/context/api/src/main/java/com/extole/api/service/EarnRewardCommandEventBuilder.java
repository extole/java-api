package com.extole.api.service;

public interface EarnRewardCommandEventBuilder {

    EarnRewardCommandEventBuilder withRewardName(String name);

    EarnRewardCommandEventBuilder addData(String key, String value);

    EarnRewardCommandEventBuilder withRewardSupplierId(String rewardSupplierId);

    EarnRewardCommandEventBuilder withEarnedEventValue(String earnedEventValue);

    EarnRewardCommandEventBuilder addTag(String tag);

    void send();
}
