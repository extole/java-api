package com.extole.api.step.campaign.step.trigger;

public interface StepTrigger {

    enum StepTriggerType {

        SHARE,
        EVENT,
        SCORE,
        ZONE_STATE,
        REFERRED_BY_EVENT,
        LEGACY_QUALITY,
        EXPRESSION,
        ACCESS,
        DATA_INTELLIGENCE_EVENT,
        HAS_PRIOR_STEP,
        MAXMIND,
        REWARD_EVENT,
        SEND_REWARD_EVENT,
        AUDIENCE_MEMBERSHIP,
        AUDIENCE_MEMBERSHIP_EVENT,
        HAS_PRIOR_REWARD,
        INCENTIVIZED_EVENT,
        HAS_IDENTITY,
        CLIENT_DOMAIN,
        LEGACY_LABEL_TARGETING,
        ADD_SHAREABLE,
        REFERRAL,
        GROUP

    }

    enum StepTriggerPhase {

        MATCHING,
        QUALIFYING,
        QUALITY

    }

    String getId();

    String getType();

    String getPhase();

    String getName();

    boolean getNegated();

}
