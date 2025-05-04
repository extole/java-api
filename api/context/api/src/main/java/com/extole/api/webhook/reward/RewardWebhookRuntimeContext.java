package com.extole.api.webhook.reward;

import com.extole.api.client.security.key.ClientKeyApiException;
import com.extole.api.webhook.ConsumerWebhookRuntimeContext;
import com.extole.api.webhook.WebhookRequestBuilder;
import com.extole.api.webhook.reward.event.PublicReward;

public interface RewardWebhookRuntimeContext extends ConsumerWebhookRuntimeContext {

    PublicReward getReward();

    WebhookRequestBuilder createLegacyRequestBuilderWithDefaults() throws ClientKeyApiException;

}
