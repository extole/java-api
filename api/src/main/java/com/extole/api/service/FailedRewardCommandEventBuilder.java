package com.extole.api.service;

public interface FailedRewardCommandEventBuilder {

    FailedRewardCommandEventBuilder withMessage(String message);

    void send();
}
