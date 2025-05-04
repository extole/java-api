package com.extole.client.rest.campaign.component.asset;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class CampaignComponentAssetUpdateRequest {

    private static final String NAME = "name";
    private static final String TAGS = "tags";
    private static final String DESCRIPTION = "description";

    private final Omissible<String> name;
    private final Omissible<Set<String>> tags;
    private final Omissible<Optional<String>> description;

    @JsonCreator
    public CampaignComponentAssetUpdateRequest(@JsonProperty(NAME) Omissible<String> name,
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @JsonProperty(DESCRIPTION) Omissible<Optional<String>> description) {
        this.name = name;
        this.tags = tags;
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty(NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(DESCRIPTION)
    public Omissible<Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private Omissible<String> name = Omissible.omitted();
        private Omissible<Optional<String>> description = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();

        private Builder() {

        }

        public Builder withName(String name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withDescription(Optional<String> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public CampaignComponentAssetUpdateRequest build() {
            return new CampaignComponentAssetUpdateRequest(name,
                tags,
                description);
        }
    }

}
