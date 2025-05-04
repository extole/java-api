package com.extole.api.notification.subscription;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.GlobalContext;
import com.extole.api.event.client.ClientEvent;

@Schema
public interface UserSubscriptionFilterContext extends GlobalContext {

    ClientEvent getEvent();

    UserSubscription getUserSubscription();

}
