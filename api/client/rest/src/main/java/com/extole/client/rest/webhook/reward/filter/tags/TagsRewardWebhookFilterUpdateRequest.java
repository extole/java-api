package com.extole.client.rest.webhook.reward.filter.tags;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class TagsRewardWebhookFilterUpdateRequest {

    private static final String TAGS = "tags";

    private final Omissible<Set<String>> tags;

    public TagsRewardWebhookFilterUpdateRequest(
        @JsonProperty(TAGS) Omissible<Set<String>> tags) {
        this.tags = tags;
    }

    @JsonProperty(TAGS)
    public Omissible<Set<String>> getTags() {
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

        private Omissible<Set<String>> tags = Omissible.omitted();

        private Builder() {

        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public TagsRewardWebhookFilterUpdateRequest build() {
            return new TagsRewardWebhookFilterUpdateRequest(tags);
        }
    }
}
