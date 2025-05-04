package com.extole.client.rest.webhook.reward.filter.tags;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TagsRewardWebhookFilterRestException
    extends ExtoleRestException {

    public static final ErrorCode<TagsRewardWebhookFilterRestException> TAGS_REWARD_WEBHOOK_FILTER_NOT_FOUND =
        new ErrorCode<>(
            "tags_reward_webhook_filter_not_found", 400, "Tags Reward Webhook filter is not found",
            "webhook_id",
            "filter_id");

    public static final ErrorCode<TagsRewardWebhookFilterRestException> TAGS_REWARD_WEBHOOK_FILTER_REWARD_TAGS_MISSING =
        new ErrorCode<>(
            "tags_reward_webhook_filter_missing_reward_tags", 400,
            "Tags Reward Webhook filter should specify reward tags",
            "webhook_id");

    public static final ErrorCode<TagsRewardWebhookFilterRestException> TAGS_REWARD_WEBHOOK_FILTER_REWARD_TAGS_EMPTY =
        new ErrorCode<>(
            "tags_reward_webhook_filter_empty_reward_tags", 400,
            "Tags Reward Webhook filter should specify at least one reward tags",
            "webhook_id");

    public static final ErrorCode<TagsRewardWebhookFilterRestException> TAGS_REWARD_WEBHOOK_FILTER_BUILD_FAILED =
        new ErrorCode<>("webhook_build_failed", 400, "Webhook build failed",
            "webhook_id", "evaluatable_name", "evaluatable");

    public TagsRewardWebhookFilterRestException(String uniqueId,
        ErrorCode<TagsRewardWebhookFilterRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
