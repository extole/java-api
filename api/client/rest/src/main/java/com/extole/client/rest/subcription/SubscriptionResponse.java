package com.extole.client.rest.subcription;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.subcription.channel.response.SubscriptionChannelResponse;
import com.extole.common.lang.ToString;

public class SubscriptionResponse {
    private static final String SUBSCRIPTION_ID = "subscription_id";
    private static final String HAVING_ALL_TAGS = "having_all_tags";
    private static final String FILTERING_LEVEL = "filtering_level";
    private static final String DEDUPE_DURATION_MS = "dedupe_duration_ms";
    private static final String USER = "user";
    private static final String CHANNELS = "channels";

    private final String subscriptionId;
    private final Set<String> tags;
    private final FilteringLevel filteringLevel;
    private final long dedupeDurationMs;
    private final SubscriptionUserResponse user;
    private final List<SubscriptionChannelResponse> channels;

    @JsonCreator
    public SubscriptionResponse(
        @JsonProperty(SUBSCRIPTION_ID) String subscriptionId,
        @JsonProperty(HAVING_ALL_TAGS) Set<String> tags,
        @JsonProperty(FILTERING_LEVEL) FilteringLevel filteringLevel,
        @JsonProperty(DEDUPE_DURATION_MS) long dedupeDurationMs,
        @JsonProperty(CHANNELS) List<SubscriptionChannelResponse> channels,
        @JsonProperty(USER) SubscriptionUserResponse user) {
        this.subscriptionId = subscriptionId;
        this.tags = tags;
        this.filteringLevel = filteringLevel;
        this.dedupeDurationMs = dedupeDurationMs;
        this.user = user;
        this.channels = channels;
    }

    @JsonProperty(SUBSCRIPTION_ID)
    public String getSubscriptionId() {
        return subscriptionId;
    }

    @JsonProperty(HAVING_ALL_TAGS)
    public Set<String> getHavingAllTags() {
        return tags;
    }

    @JsonProperty(FILTERING_LEVEL)
    public FilteringLevel getLevel() {
        return filteringLevel;
    }

    @JsonProperty(DEDUPE_DURATION_MS)
    public long getDedupeDurationMs() {
        return dedupeDurationMs;
    }

    @JsonProperty(CHANNELS)
    public List<SubscriptionChannelResponse> getChannels() {
        return channels;
    }

    @JsonProperty(USER)
    public SubscriptionUserResponse getUser() {
        return user;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
