package com.extole.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface UserSubscription extends EventEntity {
    String getId();

    String getUserId();

    String[] getHavingAllTags();

    String getFilteringLevel();

    String getFilterExpression();

    UserSubscriptionChannelEntity[] getChannels();

    long getDedupeDuration();

    String getCreatedDate();

    String getUpdatedDate();

    interface UserSubscriptionChannelEntity {
        String getId();

        String getType();
    }

}
