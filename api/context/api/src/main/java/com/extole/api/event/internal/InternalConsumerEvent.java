package com.extole.api.event.internal;

import com.extole.api.event.ConsumerEvent;

public interface InternalConsumerEvent extends ConsumerEvent {

    String getName();

    String[] getLabels();

    String getCampaignId();

}
