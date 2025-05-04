package com.extole.api.service;

public interface RedeemRewardCommandEventBuilder {

    RedeemRewardCommandEventBuilder withPartnerEventId(String message);

    RedeemRewardCommandEventBuilder addData(String key, Object value);

    void send();
}
