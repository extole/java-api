package com.extole.client.rest.subcription;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.notification.subscription.UserSubscriptionFilterContext;
import com.extole.client.rest.subcription.channel.request.SubscriptionChannelRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.RuntimeEvaluatable;

public class UserSubscriptionUpdateRequest {
    private static final String JSON_HAVING_ALL_TAGS = "having_all_tags";
    private static final String JSON_FILTERING_LEVEL = "filtering_level";
    private static final String JSON_FILTER_EXPRESSION = "filter_expression";
    private static final String JSON_DEDUPE_DURATION_MS = "dedupe_duration_ms";
    private static final String JSON_CHANNELS = "channels";

    private final Omissible<Set<String>> havingAllTags;
    private final Omissible<FilteringLevel> filteringLevel;
    private final Omissible<RuntimeEvaluatable<UserSubscriptionFilterContext, Boolean>> filterExpression;
    private final Omissible<Long> dedupeDurationMs;
    private final Omissible<List<SubscriptionChannelRequest>> channels;

    @JsonCreator
    public UserSubscriptionUpdateRequest(
        @JsonProperty(JSON_HAVING_ALL_TAGS) Omissible<Set<String>> havingAllTags,
        @JsonProperty(JSON_FILTERING_LEVEL) Omissible<FilteringLevel> filteringLevel,
        @JsonProperty(JSON_FILTER_EXPRESSION) Omissible<
            RuntimeEvaluatable<UserSubscriptionFilterContext, Boolean>> filterExpression,
        @JsonProperty(JSON_DEDUPE_DURATION_MS) Omissible<Long> dedupeDurationMs,
        @JsonProperty(JSON_CHANNELS) Omissible<List<SubscriptionChannelRequest>> channels) {
        this.havingAllTags = havingAllTags;
        this.filteringLevel = filteringLevel;
        this.filterExpression = filterExpression;
        this.dedupeDurationMs = dedupeDurationMs;
        this.channels = channels;
    }

    @JsonProperty(JSON_HAVING_ALL_TAGS)
    public Omissible<Set<String>> getHavingAllTags() {
        return havingAllTags;
    }

    @JsonProperty(JSON_FILTERING_LEVEL)
    public Omissible<FilteringLevel> getLevel() {
        return filteringLevel;
    }

    @JsonProperty(JSON_FILTER_EXPRESSION)
    public Omissible<RuntimeEvaluatable<UserSubscriptionFilterContext, Boolean>> getFilterExpression() {
        return filterExpression;
    }

    @JsonProperty(JSON_DEDUPE_DURATION_MS)
    public Omissible<Long> getDedupeDurationMs() {
        return dedupeDurationMs;
    }

    @JsonProperty(JSON_CHANNELS)
    public Omissible<List<SubscriptionChannelRequest>> getChannels() {
        return channels;
    }

    public static final class Builder {
        private Omissible<Set<String>> tags = Omissible.omitted();
        private Omissible<FilteringLevel> filteringLevel = Omissible.omitted();
        private Omissible<RuntimeEvaluatable<UserSubscriptionFilterContext, Boolean>> filterExpression =
            Omissible.omitted();
        private Omissible<Long> dedupeDurationMs = Omissible.omitted();
        private Omissible<List<SubscriptionChannelRequest>> channels = Omissible.omitted();

        public Builder withTags(List<String> tags) {
            this.tags = Omissible.of(new HashSet<>(tags));
            return this;
        }

        public Builder withLevel(FilteringLevel filteringLevel) {
            this.filteringLevel = Omissible.of(filteringLevel);
            return this;
        }

        public Builder withFilterExpression(
            RuntimeEvaluatable<UserSubscriptionFilterContext, Boolean> filterExpression) {
            this.filterExpression = Omissible.of(filterExpression);
            return this;
        }

        public Builder withDedupeDurationMs(long dedupeDurationMs) {
            this.dedupeDurationMs = Omissible.of(Long.valueOf(dedupeDurationMs));
            return this;
        }

        public Builder withChannels(List<SubscriptionChannelRequest> channels) {
            this.channels = Omissible.of(channels);
            return this;
        }

        public UserSubscriptionUpdateRequest build() {
            return new UserSubscriptionUpdateRequest(tags, filteringLevel, filterExpression, dedupeDurationMs,
                channels);
        }
    }
}
