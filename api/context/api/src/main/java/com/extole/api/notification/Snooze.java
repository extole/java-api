package com.extole.api.notification;

import javax.annotation.Nullable;

public interface Snooze {

    String getClientId();

    String getId();

    String getUserId();

    String getExpiresAt();

    String getCreatedDate();

    @Nullable
    String getComment();

    String[] getHavingExactlyTags();
}
