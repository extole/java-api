package com.extole.api.impl.model;

import com.extole.api.model.UserSubscription;
import com.extole.event.model.change.subscription.UserSubscriptionPojo;

final class UserSubscriptionImpl implements UserSubscription {
    private final UserSubscriptionPojo userSubscription;
    private final String[] havingAllTags;

    UserSubscriptionImpl(UserSubscriptionPojo userSubscription) {
        this.userSubscription = userSubscription;
        this.havingAllTags = userSubscription.getHavingAllTags().toArray(String[]::new);
    }

    @Override
    public String getId() {
        return userSubscription.getId().getValue();
    }

    @Override
    public String getUserId() {
        return userSubscription.getUserId().getValue();
    }

    @Override
    public String[] getHavingAllTags() {
        return havingAllTags;
    }

    @Override
    public String getFilteringLevel() {
        return userSubscription.getFilteringLevel().name();
    }

    @Override
    public String getFilterExpression() {
        return userSubscription.getFilterExpression().toString();
    }

    @Override
    public UserSubscriptionChannelEntity[] getChannels() {
        return userSubscription.getChannels().stream()
            .map(value -> new UserSubscriptionChannelEntityImpl(value.getId().getValue(), value.getType().name()))
            .toArray(UserSubscriptionChannelEntity[]::new);
    }

    @Override
    public long getDedupeDuration() {
        return userSubscription.getDedupeDurationMs();
    }

    @Override
    public String getCreatedDate() {
        return userSubscription.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return userSubscription.getUpdatedDate().toString();
    }

    private static final class UserSubscriptionChannelEntityImpl implements UserSubscriptionChannelEntity {
        private final String id;
        private final String type;

        private UserSubscriptionChannelEntityImpl(String id, String type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getType() {
            return type;
        }
    }
}
