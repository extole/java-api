package com.extole.api.event.internal;

import javax.annotation.Nullable;

import com.extole.api.event.ConsumerEvent;

public interface InternalConsumerEvent extends ConsumerEvent {

    String getName();

    String[] getLabels();

    @Nullable
    String getCampaignId();

}
