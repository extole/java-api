package com.extole.api.model.campaign;

import java.math.BigDecimal;

import javax.annotation.Nullable;

public interface RewardRule {

    String getId();

    String getRewardSupplierId();

    String getRewardee();

    int getReferralsPerReward();

    BigDecimal getMinCartValue();

    String getRuleActionType();

    String[] getRewardSlots();

    int getRewardCountLimit();

    int getRewardCountSinceMonth();

    int getRewardCountSinceDays();

    BigDecimal getRewardValueLimit();

    int getRewardValueSinceMonth();

    int getRewardValueSinceDays();

    int getRewardEveryXFriendActions();

    boolean isUniqueFriendRequired();

    boolean isReferralLoopAllowed();

    boolean isEmailRequired();

    String getDataAttributeName();

    String getDataAttributeValue();

    @Nullable
    String getExpression();

    String getDataAttributeMatcherType();

    boolean isRewardCountingBasedOnPartnerUserId();

    String getCreatedDate();

    String getUpdatedDate();
}
