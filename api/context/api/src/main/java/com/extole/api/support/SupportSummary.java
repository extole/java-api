package com.extole.api.support;

import javax.annotation.Nullable;

public interface SupportSummary {

    @Nullable
    String getSalesforceAccountId();

    @Nullable
    String getSlackChannelName();

    @Nullable
    String getExternalSlackChannelName();

    @Nullable
    String getCsmUserId();

    @Nullable
    String getCsmEmail();

    @Nullable
    String getCsmFirstName();

    @Nullable
    String getCsmLastName();

    @Nullable
    String getSupportUserId();

    @Nullable
    String getSupportEmail();

    @Nullable
    String getSupportFirstName();

    @Nullable
    String getSupportLastName();
}
