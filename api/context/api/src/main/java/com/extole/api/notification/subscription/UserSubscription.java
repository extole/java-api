package com.extole.api.notification.subscription;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.notification.subscription.channel.UserSubscriptionChannel;

@Schema
public interface UserSubscription {

    String getUserId();

    String getClientId();

    String getCreatedDate();

    String getUpdatedDate();

    Set<String> getSubscriptionTags();

    String getFilteringLevel();

    List<UserSubscriptionChannel> getChannels();

    Duration getDedupeDuration();

}
