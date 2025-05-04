package com.extole.api.impl.service;

import com.extole.api.service.FulfillRewardCommandEventBuilder;
import com.extole.event.reward.command.fulfill.FulfillRewardCommandEventProducer;

public class FulfillRewardCommandEventBuilderImpl
    implements FulfillRewardCommandEventBuilder {

    private final FulfillRewardCommandEventProducer.FulfillRewardCommandEventBuilder eventBuilder;

    public FulfillRewardCommandEventBuilderImpl(
        FulfillRewardCommandEventProducer.FulfillRewardCommandEventBuilder eventBuilder) {
        this.eventBuilder = eventBuilder;
    }

    @Override
    public FulfillRewardCommandEventBuilder withMessage(String message) {
        this.eventBuilder.withMessage(message);
        return this;
    }

    @Override
    public FulfillRewardCommandEventBuilder withSuccess(Boolean success) {
        this.eventBuilder.withSuccess(success);
        return this;
    }

    @Override
    public FulfillRewardCommandEventBuilder withPartnerRewardId(String partnerRewardId) {
        this.eventBuilder.withPartnerRewardId(partnerRewardId);
        return this;
    }

    @Override
    public void send() {
        this.eventBuilder.send();
    }
}
