package com.extole.client.rest.impl.webhook.reward.filter.expression;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.webhook.reward.filter.expression.ExpressionRewardWebhookFilterResponse;
import com.extole.model.entity.webhook.reward.filter.expression.ExpressionRewardWebhookFilter;

@Component
public class ExpressionRewardWebhookFilterRestMapper {

    public ExpressionRewardWebhookFilterResponse toRewardWebhookExpressionFilterResponse(
        ExpressionRewardWebhookFilter filter, ZoneId timeZone) {
        return new ExpressionRewardWebhookFilterResponse(filter.getId().getValue(), filter.getExpression(),
            filter.getCreatedAt().atZone(timeZone), filter.getUpdatedAt().atZone(timeZone));
    }
}
