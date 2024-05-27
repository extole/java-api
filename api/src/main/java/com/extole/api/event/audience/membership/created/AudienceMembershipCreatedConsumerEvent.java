package com.extole.api.event.audience.membership.created;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.ConsumerEvent;
import com.extole.api.event.audience.membership.Audience;

@Schema
public interface AudienceMembershipCreatedConsumerEvent extends ConsumerEvent {

    Audience getAudience();

}
