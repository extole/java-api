package com.extole.client.rest.webhook.reward.filter.tags;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class TagsRewardWebhookFilterCreateRequest {

    private static final String TAGS = "tags";

    private final Set<String> tags;

    public TagsRewardWebhookFilterCreateRequest(@JsonProperty(TAGS) Set<String> tags) {
        this.tags = tags;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder newRequestBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private Set<String> tags;

        private Builder() {

        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public TagsRewardWebhookFilterCreateRequest build() {
            return new TagsRewardWebhookFilterCreateRequest(tags);
        }
    }
}
