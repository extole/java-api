package com.extole.client.rest.subcription;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.notification.subscription.UserSubscriptionFilterContext;
import com.extole.client.rest.subcription.channel.request.SubscriptionChannelRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.RuntimeEvaluatable;

public class UserSubscriptionRequest {
    private static final String JSON_HAVING_ALL_TAGS = "having_all_tags";
    private static final String JSON_FILTERING_LEVEL = "filtering_level";
    private static final String JSON_FILTER_EXPRESSION = "filter_expression";
    private static final String JSON_DEDUPE_DURATION_MS = "dedupe_duration_ms";
    private static final String JSON_CHANNELS = "channels";

    private final Set<String> havingAllTags;
    private final FilteringLevel filteringLevel;
    private final Omissible<RuntimeEvaluatable<UserSubscriptionFilterContext, Boolean>> filterExpression;
    private final Omissible<Long> dedupeDurationMs;
    private final Omissible<List<SubscriptionChannelRequest>> channels;

    @JsonCreator
    public UserSubscriptionRequest(
        @JsonProperty(JSON_HAVING_ALL_TAGS) Set<String> havingAllTags,
        @JsonProperty(JSON_FILTERING_LEVEL) Optional<FilteringLevel> filteringLevel,
        @JsonProperty(JSON_FILTER_EXPRESSION) Omissible<
            RuntimeEvaluatable<UserSubscriptionFilterContext, Boolean>> filterExpression,
        @JsonProperty(JSON_DEDUPE_DURATION_MS) Omissible<Long> dedupeDurationMs,
        @JsonProperty(JSON_CHANNELS) Omissible<List<SubscriptionChannelRequest>> channels) {
        this.havingAllTags = ImmutableSet.copyOf(havingAllTags);
        this.filteringLevel = filteringLevel.isPresent() ? filteringLevel.get() : FilteringLevel.SOME;
        this.filterExpression = filterExpression;
        this.dedupeDurationMs = dedupeDurationMs;
        this.channels = channels;
    }

    @JsonProperty(JSON_HAVING_ALL_TAGS)
    public Set<String> getHavingAllTags() {
        return havingAllTags;
    }

    @JsonProperty(JSON_FILTERING_LEVEL)
    public FilteringLevel getLevel() {
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
        private final Set<String> tags = new HashSet<>();
        private Optional<FilteringLevel> level = Optional.empty();
        private Omissible<RuntimeEvaluatable<UserSubscriptionFilterContext, Boolean>> filterExpression = Omissible
            .omitted();
        private Omissible<Long> dedupeDurationMs = Omissible.omitted();
        private Omissible<List<SubscriptionChannelRequest>> channels = Omissible.omitted();

        public Builder addTag(String tag) {
            this.tags.add(tag);
            return this;
        }

        public Builder withLevel(FilteringLevel level) {
            this.level = Optional.of(level);
            return this;
        }

        public Builder
            withFilterExpression(RuntimeEvaluatable<UserSubscriptionFilterContext, Boolean> filterExpression) {
            this.filterExpression = Omissible.of(filterExpression);
            return this;
        }

        public Builder withDedupeDurationMs(long dedupeDurationMs) {
            this.dedupeDurationMs = Omissible.of(Long.valueOf(dedupeDurationMs));
            return this;
        }

        public Builder withChannels(List<SubscriptionChannelRequest> channels) {
            if (channels == null) {
                this.channels = Omissible.omitted();
            } else {
                this.channels = Omissible.of(new ArrayList<>(channels));
            }
            return this;
        }

        public Builder addChannel(SubscriptionChannelRequest channel) {
            if (this.channels.isOmitted()) {
                ArrayList<SubscriptionChannelRequest> list = new ArrayList<>();
                list.add(channel);
                this.channels = Omissible.of(list);
            } else {
                this.channels.getValue().add(channel);
            }
            return this;
        }

        public UserSubscriptionRequest build() {
            return new UserSubscriptionRequest(tags, level, filterExpression, dedupeDurationMs, channels);
        }

    }

}
