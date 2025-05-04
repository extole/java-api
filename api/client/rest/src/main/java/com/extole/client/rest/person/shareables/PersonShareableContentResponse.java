package com.extole.client.rest.person.shareables;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class PersonShareableContentResponse {
    private final Optional<String> partnerContentId;
    private final Optional<String> title;
    private final Optional<String> imageUrl;
    private final Optional<String> description;
    private final Optional<String> url;

    @JsonCreator
    public PersonShareableContentResponse(
        @JsonProperty("partner_content_id") Optional<String> partnerContentId,
        @JsonProperty("title") Optional<String> title,
        @JsonProperty("image_url") Optional<String> imageUrl,
        @JsonProperty("description") Optional<String> description,
        @JsonProperty("url") Optional<String> url) {
        this.partnerContentId = partnerContentId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.url = url;
    }

    @JsonProperty("partner_content_id")
    public Optional<String> getPartnerContentId() {
        return partnerContentId;
    }

    @JsonProperty("title")
    public Optional<String> getTitle() {
        return title;
    }

    @JsonProperty("image_url")
    public Optional<String> getImageUrl() {
        return imageUrl;
    }

    @JsonProperty("description")
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty("url")
    public Optional<String> getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Optional<String> partnerContentId = Optional.empty();
        private Optional<String> title = Optional.empty();
        private Optional<String> imageUrl = Optional.empty();
        private Optional<String> description = Optional.empty();
        private Optional<String> url = Optional.empty();

        private Builder() {
        }

        public Builder withPartnerContentId(String partnerContentId) {
            this.partnerContentId = Optional.ofNullable(partnerContentId);
            return this;
        }

        public Builder withTitle(String title) {
            this.title = Optional.ofNullable(title);
            return this;
        }

        public Builder withImageUrl(String imageUrl) {
            this.imageUrl = Optional.ofNullable(imageUrl);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Optional.ofNullable(description);
            return this;
        }

        public Builder withUrl(String url) {
            this.url = Optional.ofNullable(url);
            return this;
        }

        public PersonShareableContentResponse build() {
            return new PersonShareableContentResponse(partnerContentId, title, imageUrl, description, url);
        }
    }
}
