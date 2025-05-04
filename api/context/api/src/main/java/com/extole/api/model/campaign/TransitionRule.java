package com.extole.api.model.campaign;

public interface TransitionRule {

    String getId();

    String getActionType();

    boolean getApproveLowQuality();

    boolean getApproveHighQuality();

    long getTransitionPeriodMilliseconds();

    String getCreatedDate();

    String getUpdatedDate();
}
