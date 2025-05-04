package com.extole.client.rest.impl.webhook.reward.filter;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.webhook.reward.filter.RewardWebhookFilterResponse;
import com.extole.client.rest.webhook.reward.filter.RewardWebhookFilterType;
import com.extole.model.entity.webhook.reward.filter.RewardWebhookFilter;

@Component
public class RewardWebhookFilterRestMapper {

    public RewardWebhookFilterResponse toRewardWebhookFilterResponse(RewardWebhookFilter filter, ZoneId timeZone) {
        return new RewardWebhookFilterResponse(filter.getId().getValue(),
            RewardWebhookFilterType.valueOf(filter.getType().name()),
            filter.getCreatedAt().atZone(timeZone),
            filter.getUpdatedAt().atZone(timeZone));
    }

}
