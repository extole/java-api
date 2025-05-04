package com.extole.api.impl.service;

import java.util.HashMap;
import java.util.Map;

import com.extole.api.service.RedeemRewardCommandEventBuilder;
import com.extole.event.reward.command.redeem.RedeemRewardCommandEventProducer;

public class RedeemRewardCommandEventBuilderImpl
    implements RedeemRewardCommandEventBuilder {

    private final RedeemRewardCommandEventProducer.RedeemRewardCommandEventBuilder eventBuilder;
    private final Map<String, Object> data;

    public RedeemRewardCommandEventBuilderImpl(
        RedeemRewardCommandEventProducer.RedeemRewardCommandEventBuilder eventBuilder) {
        this.eventBuilder = eventBuilder;
        this.data = new HashMap<>();
    }

    @Override
    public RedeemRewardCommandEventBuilder withPartnerEventId(String partnerEventId) {
        eventBuilder.withPartnerEventId(partnerEventId);
        return this;
    }

    @Override
    public RedeemRewardCommandEventBuilder addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    @Override
    public void send() {
        eventBuilder.withData(data);
        this.eventBuilder.send();
    }
}
