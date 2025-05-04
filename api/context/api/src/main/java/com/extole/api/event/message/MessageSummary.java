package com.extole.api.event.message;

import java.util.Map;

import javax.annotation.Nullable;

public interface MessageSummary {

    String getClientId();

    String getMessageId();

    String getZoneName();

    @Nullable
    String getProgramLabel();

    @Nullable
    String getCampaignId();

    @Nullable
    String getContainer();

    @Nullable
    String getOptoutList();

    @Nullable
    String getNormalizedEmailFrom();

    @Nullable
    String getNormalizedEmailSentAs();

    @Nullable
    String getNormalizedEmailTo();

    @Nullable
    String getEmailTo();

    @Nullable
    String getEmailSubject();

    @Nullable
    String getDoNotSendReason();

    String getStatus();

    Map<String, Object> getData();

    String getTriggeredDate();

    @Nullable
    String getSentDate();

    @Nullable
    String getRecipientId();
}
