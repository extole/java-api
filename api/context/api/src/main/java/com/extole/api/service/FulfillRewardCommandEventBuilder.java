package com.extole.api.service;

public interface FulfillRewardCommandEventBuilder {

    FulfillRewardCommandEventBuilder withMessage(String message);

    FulfillRewardCommandEventBuilder withSuccess(Boolean success);

    FulfillRewardCommandEventBuilder withPartnerRewardId(String partnerRewardId);

    void send();
}
