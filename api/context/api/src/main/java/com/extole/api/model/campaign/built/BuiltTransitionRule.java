package com.extole.api.model.campaign.built;

public interface BuiltTransitionRule {

    String getId();

    String getActionType();

    boolean getApproveLowQuality();

    boolean getApproveHighQuality();

    long getTransitionPeriodInMilliseconds();

    String getCreatedDate();

    String getUpdatedDate();
}
