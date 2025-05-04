package com.extole.client.rest.subcription;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.notification.subscription.UserSubscriptionFilterContext;
import com.extole.client.rest.subcription.channel.response.SubscriptionChannelResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.RuntimeEvaluatable;

public class UserSubscriptionResponse {
    private static final String JSON_SUBSCRIPTION_ID = "subscription_id";
    private static final String JSON_HAVING_ALL_TAGS = "having_all_tags";
    private static final String JSON_FILTERING_LEVEL = "filtering_level";
    private static final String JSON_FILTER_EXPRESSION = "filter_expression";
    private static final String JSON_DEDUPE_DURATION_MS = "dedupe_duration_ms";
    private static final String JSON_CHANNELS = "channels";

    private final String subscriptionId;
    private final Set<String> tags;
    private final FilteringLevel filteringLevel;
    private final RuntimeEvaluatable<UserSubscriptionFilterContext, Boolean> filterExpression;
    private final long dedupeDurationMs;
    private final List<SubscriptionChannelResponse> channels;

    @JsonCreator
    public UserSubscriptionResponse(
        @JsonProperty(JSON_SUBSCRIPTION_ID) String subscriptionId,
        @JsonProperty(JSON_HAVING_ALL_TAGS) Set<String> tags,
        @JsonProperty(JSON_FILTERING_LEVEL) FilteringLevel filteringLevel,
        @JsonProperty(JSON_FILTER_EXPRESSION) RuntimeEvaluatable<UserSubscriptionFilterContext,
            Boolean> filterExpression,
        @JsonProperty(JSON_DEDUPE_DURATION_MS) long dedupeDurationMs,
        @JsonProperty(JSON_CHANNELS) List<SubscriptionChannelResponse> channels) {
        this.subscriptionId = subscriptionId;
        this.tags = tags;
        this.filteringLevel = filteringLevel;
        this.filterExpression = filterExpression;
        this.dedupeDurationMs = dedupeDurationMs;
        this.channels = channels;
    }

    @JsonProperty(JSON_SUBSCRIPTION_ID)
    public String getSubscriptionId() {
        return subscriptionId;
    }

    @JsonProperty(JSON_HAVING_ALL_TAGS)
    public Set<String> getHavingAllTags() {
        return tags;
    }

    @JsonProperty(JSON_FILTERING_LEVEL)
    public FilteringLevel getLevel() {
        return filteringLevel;
    }

    @JsonProperty(JSON_FILTER_EXPRESSION)
    public RuntimeEvaluatable<UserSubscriptionFilterContext, Boolean> getFilterExpression() {
        return filterExpression;
    }

    @JsonProperty(JSON_DEDUPE_DURATION_MS)
    public long getDedupeDurationMs() {
        return dedupeDurationMs;
    }

    @JsonProperty(JSON_CHANNELS)
    public List<SubscriptionChannelResponse> getChannels() {
        return channels;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
