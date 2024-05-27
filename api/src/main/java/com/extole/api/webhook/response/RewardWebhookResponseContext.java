package com.extole.api.webhook.response;

import com.extole.api.service.FailedRewardCommandEventBuilder;
import com.extole.api.service.FulfillRewardCommandEventBuilder;
import com.extole.api.webhook.reward.event.PublicReward;

public interface RewardWebhookResponseContext extends WebhookResponseContext {

    FulfillRewardCommandEventBuilder createFulfillRewardCommandEventBuilder();

    FailedRewardCommandEventBuilder createFailedRewardCommandEventBuilder();

    PublicReward getReward();
}
