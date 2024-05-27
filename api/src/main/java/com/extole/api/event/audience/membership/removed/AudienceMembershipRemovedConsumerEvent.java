package com.extole.api.event.audience.membership.removed;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.ConsumerEvent;
import com.extole.api.event.audience.membership.Audience;

@Schema
public interface AudienceMembershipRemovedConsumerEvent extends ConsumerEvent {

    Audience getAudience();

}
