package com.extole.api.notification.subscription.channel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface UserSubscriptionChannel {

    String getId();

    String getType();

}
