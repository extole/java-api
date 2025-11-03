package com.extole.api.step.campaign.step.action;

public interface StepAction {

    enum ActionType {

        APPROVE,
        CANCEL_REWARD,
        CREATE_MEMBERSHIP,
        CREATIVE,
        DATA_INTELLIGENCE,
        DECLINE,
        DISPLAY,
        EARN_REWARD,
        EMAIL,
        EXPRESSION,
        FIRE_AS_PERSON,
        FULFILL_REWARD,
        INCENTIVIZE,
        INCENTIVIZE_STATUS_UPDATE,
        REDEEM_REWARD,
        REMOVE_MEMBERSHIP,
        REVOKE_REWARD,
        SCHEDULE,
        SHARE_EVENT,
        SIGNAL,
        SIGNAL_V1,
        STEP_SIGNAL,
        WEBHOOK

    }

    enum ActionQuality {

        HIGH, LOW, ALWAYS

    }

    String getId();

    String getType();

    String getQuality();

}
