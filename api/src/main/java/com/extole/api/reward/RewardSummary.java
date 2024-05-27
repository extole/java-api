package com.extole.api.reward;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public interface RewardSummary {

    String getRewardId();

    String getClientId();

    @Nullable
    String getPartnerRewardId();

    String getPersonId();

    BigDecimal getFaceValue();

    String getFaceValueType();

    String getRewardSupplierId();

    String getRootEventId();

    String getCauseEventId();

    String getCampaignId();

    String getProgram();

    @Nullable
    StepEventContext getEarnedStepEventContext();

    Map<String, Object> getData();

    List<String> getTags();

    String getCurrentState();

    @Nullable
    String getRewardEarnedDate();

    @Nullable
    String getRewardFulfilledDate();

    @Nullable
    String getRewardSentDate();

    @Nullable
    String getRewardFailedDate();

    @Nullable
    String getRewardCanceledDate();

    @Nullable
    String getRewardRevokedDate();

    @Nullable
    String getRewardRedeemedDate();
}
