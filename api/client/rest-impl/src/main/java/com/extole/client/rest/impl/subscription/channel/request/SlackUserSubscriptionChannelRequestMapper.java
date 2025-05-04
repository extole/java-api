package com.extole.client.rest.impl.subscription.channel.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.subcription.channel.SlackUserSubscriptionChannelValidationRestException;
import com.extole.client.rest.subcription.channel.UserSubscriptionChannelValidationRestException;
import com.extole.client.rest.subcription.channel.request.SlackSubscriptionChannelRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.subscription.ChannelType;
import com.extole.model.service.subscription.UserSubscriptionBuilder;
import com.extole.model.service.subscription.channel.slack.SlackUserSubscriptionChannelBuilder;
import com.extole.model.service.subscription.channel.slack.SlackUserSubscriptionChannelMalformedUrlException;
import com.extole.model.service.subscription.channel.slack.SlackUserSubscriptionChannelMissingWebhookUrlException;

@Component
public class SlackUserSubscriptionChannelRequestMapper
    implements UserSubscriptionChannelRequestMapper<SlackSubscriptionChannelRequest> {

    @Override
    public void update(UserSubscriptionBuilder builder, SlackSubscriptionChannelRequest request)
        throws UserSubscriptionChannelValidationRestException {
        SlackUserSubscriptionChannelBuilder slackChannelBuilder = builder.addChannel(ChannelType.SLACK);
        try {
            slackChannelBuilder.withWebhookUrl(request.getWebhookUrl());
        } catch (SlackUserSubscriptionChannelMissingWebhookUrlException e) {
            throw RestExceptionBuilder
                .newBuilder(SlackUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(SlackUserSubscriptionChannelValidationRestException.MISSING_WEBHOOK_URL)
                .withCause(e)
                .build();
        } catch (SlackUserSubscriptionChannelMalformedUrlException e) {
            throw RestExceptionBuilder
                .newBuilder(SlackUserSubscriptionChannelValidationRestException.class)
                .withErrorCode(SlackUserSubscriptionChannelValidationRestException.MALFORMED_WEBHOOK_URL)
                .withCause(e)
                .build();
        }
    }

    @Override
    public com.extole.client.rest.subcription.channel.ChannelType getType() {
        return com.extole.client.rest.subcription.channel.ChannelType.SLACK;
    }
}
