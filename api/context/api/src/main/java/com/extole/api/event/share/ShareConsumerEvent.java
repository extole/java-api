package com.extole.api.event.share;

import javax.annotation.Nullable;

import com.extole.api.event.ConsumerEvent;
import com.extole.api.event.ReferralContext;

public interface ShareConsumerEvent extends ConsumerEvent {

    String getShareId();

    String getQuality();

    @Nullable
    ReferralContext getReferralContext();

}
