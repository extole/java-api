package com.extole.api.event.internal.message;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.ReferralContext;
import com.extole.api.event.internal.InternalConsumerEvent;

@Schema
public interface MessageConsumerEvent extends InternalConsumerEvent {

    String getMessageEventType();

    @Nullable
    ReferralContext getReferralContext();

    String getMessageId();

}
