package com.extole.client.rest.impl.webhook.reward.filter.tags;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.webhook.reward.filter.tags.TagsRewardWebhookFilterResponse;
import com.extole.model.entity.webhook.reward.filter.tags.TagsRewardWebhookFilter;

@Component
public class TagsRewardWebhookFilterRestMapper {

    public TagsRewardWebhookFilterResponse toRewardWebhookTagsFilterResponse(TagsRewardWebhookFilter filter,
        ZoneId timeZone) {
        return new TagsRewardWebhookFilterResponse(filter.getId().getValue(),
            filter.getTags(),
            filter.getCreatedAt().atZone(timeZone),
            filter.getUpdatedAt().atZone(timeZone));
    }
}
