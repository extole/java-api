package com.extole.client.rest.shareable;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public final class ClientShareableContentRequest {
    private final Omissible<Optional<String>> partnerContentId;
    private final Omissible<Optional<String>> title;
    private final Omissible<Optional<String>> imageUrl;
    private final Omissible<Optional<String>> description;
    private final Omissible<Optional<String>> url;

    @JsonCreator
    private ClientShareableContentRequest(
        @JsonProperty("partner_content_id") Omissible<Optional<String>> partnerContentId,
        @JsonProperty("title") Omissible<Optional<String>> title,
        @JsonProperty("image_url") Omissible<Optional<String>> imageUrl,
        @JsonProperty("description") Omissible<Optional<String>> description,
        @JsonProperty("url") Omissible<Optional<String>> url) {
        this.partnerContentId = partnerContentId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.url = url;
    }

    @JsonProperty("partner_content_id")
    public Omissible<Optional<String>> getPartnerContentId() {
        return partnerContentId;
    }

    @JsonProperty("title")
    public Omissible<Optional<String>> getTitle() {
        return title;
    }

    @JsonProperty("image_url")
    public Omissible<Optional<String>> getImageUrl() {
        return imageUrl;
    }

    @JsonProperty("description")
    public Omissible<Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty("url")
    public Omissible<Optional<String>> getUrl() {
        return url;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Optional<String>> partnerContentId = Omissible.omitted();
        private Omissible<Optional<String>> title = Omissible.omitted();
        private Omissible<Optional<String>> imageUrl = Omissible.omitted();
        private Omissible<Optional<String>> description = Omissible.omitted();
        private Omissible<Optional<String>> url = Omissible.omitted();

        private Builder() {
        }

        public Builder withPartnerContentId(String partnerContentId) {
            this.partnerContentId = Omissible.of(Optional.ofNullable(partnerContentId));
            return this;
        }

        public Builder withTitle(String title) {
            this.title = Omissible.of(Optional.ofNullable(title));
            return this;
        }

        public Builder withImageUrl(String imageUrl) {
            this.imageUrl = Omissible.of(Optional.ofNullable(imageUrl));
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Omissible.of(Optional.ofNullable(description));
            return this;
        }

        public Builder withUrl(String url) {
            this.url = Omissible.of(Optional.ofNullable(url));
            return this;
        }

        public ClientShareableContentRequest build() {
            return new ClientShareableContentRequest(partnerContentId, title, imageUrl, description, url);
        }
    }
}
