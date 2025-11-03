package com.extole.client.rest.impl.component.sharing.subscription;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.component.sharing.subscription.ComponentSubscriberResponse;
import com.extole.client.rest.component.sharing.subscription.ComponentSubscriptionResponse;
import com.extole.model.entity.component.sharing.subscription.ComponentSubscription;

@Component
public class ComponentSubscriptionRestMapper {

    public ComponentSubscriptionResponse toComponentSubscriptionResponse(ComponentSubscription componentSubscription,
        ZoneId timeZone) {
        return new ComponentSubscriptionResponse(
            componentSubscription.getId().getValue(),
            componentSubscription.getTargetClientId().getValue(),
            componentSubscription.getCreatedDate().atZone(timeZone));
    }

    public ComponentSubscriberResponse toComponentSubscriberResponse(ComponentSubscription componentSubscription,
        ZoneId timeZone) {
        return new ComponentSubscriberResponse(
            componentSubscription.getId().getValue(),
            componentSubscription.getClientId().getValue(),
            componentSubscription.getCreatedDate().atZone(timeZone));
    }

}
