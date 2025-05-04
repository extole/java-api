package com.extole.api.webhook.reward.filter;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.webhook.Webhook;
import com.extole.api.webhook.reward.event.PublicReward;

public interface RewardWebhookFilterRuntimeContext extends GlobalContext, LoggerContext {

    PublicReward getReward();

    Webhook getWebhook();
}
