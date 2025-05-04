package com.extole.api.impl.event;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;
import com.extole.event.consumer.ConsumerEventReferralContext;

public class ReferralContextImpl implements com.extole.api.event.ReferralContext {

    private final String otherPersonId;
    private final String reason;
    private final Map<String, Object> data;

    public ReferralContextImpl(ConsumerEventReferralContext context) {
        this.otherPersonId = context.getOtherPersonId().getValue();
        this.reason = context.getReason().name();
        this.data = ImmutableMap.copyOf(context.getData());
    }

    @Override
    public String getOtherPersonId() {
        return otherPersonId;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
