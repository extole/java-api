package com.extole.api.model.campaign.built;

import java.math.BigDecimal;

import javax.annotation.Nullable;

public interface BuiltRewardRule {

    String getId();

    String getRewardSupplierId();

    String getRewardee();

    Integer getReferralsPerReward();

    BigDecimal getMinCartValue();

    String getRuleActionType();

    String[] getRewardSlots();

    Integer getRewardCountLimit();

    Integer getRewardCountSinceMonth();

    Integer getRewardCountSinceDays();

    BigDecimal getRewardValueLimit();

    Integer getRewardValueSinceMonth();

    Integer getRewardValueSinceDays();

    Integer getRewardEveryXFriendActions();

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
