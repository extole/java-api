package com.extole.api.service;

public interface RewardService {

    EarnRewardCommandEventBuilder createEarnRewardCommandEventBuilder();

    FulfillRewardCommandEventBuilder createFulfillRewardCommandEventBuilder(String rewardId);

    RedeemRewardCommandEventBuilder createRedeemRewardCommandEventBuilder(String rewardId);
}
