package com.extole.api.event.internal.message;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.ConsumerEvent;
import com.extole.api.event.ReferralContext;

@Schema
public interface MessageConsumerEvent extends ConsumerEvent {

    String getMessageEventType();

    @Nullable
    ReferralContext getReferralContext();

    String getMessageId();

    String getName();

    String[] getLabels();

    @Nullable
    String getCampaignId();

}
