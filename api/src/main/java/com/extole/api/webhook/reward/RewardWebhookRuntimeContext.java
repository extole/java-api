package com.extole.api.webhook.reward;

import java.util.Map;

import com.extole.api.client.security.key.ClientKeyApiException;
import com.extole.api.webhook.ConsumerWebhookRuntimeContext;
import com.extole.api.webhook.WebhookRequestBuilder;
import com.extole.api.webhook.reward.event.PublicReward;

public interface RewardWebhookRuntimeContext extends ConsumerWebhookRuntimeContext {

    Map<String, Object> getData();

    PublicReward getReward();

    WebhookRequestBuilder createLegacyRequestBuilderWithDefaults() throws ClientKeyApiException;
}
