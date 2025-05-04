package com.extole.api.event.data_intelligence;

import javax.annotation.Nullable;

import com.extole.api.event.ReferralContext;
import com.extole.api.event.internal.InternalConsumerEvent;

public interface DataIntelligenceConsumerEvent extends InternalConsumerEvent {

    @Nullable
    ReferralContext getReferralContext();

}
