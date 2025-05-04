package com.extole.client.rest.promotion;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class PromotionLinkContentRequest {
    private static final String PROPERTY_CONTENT_ID = "content_id";
    private static final String PROPERTY_TITLE = "title";
    private static final String PROPERTY_DESCRIPTION = "description";
    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_IMAGE_URL = "image_url";

    private final Omissible<Optional<String>> contentId;
    private final Omissible<Optional<String>> title;
    private final Omissible<Optional<String>> description;
    private final Omissible<Optional<String>> url;
    private final Omissible<Optional<String>> imageUrl;

    @JsonCreator
    PromotionLinkContentRequest(
        @JsonProperty(PROPERTY_CONTENT_ID) Omissible<Optional<String>> contentId,
        @JsonProperty(PROPERTY_TITLE) Omissible<Optional<String>> title,
        @JsonProperty(PROPERTY_DESCRIPTION) Omissible<Optional<String>> description,
        @JsonProperty(PROPERTY_URL) Omissible<Optional<String>> url,
        @JsonProperty(PROPERTY_IMAGE_URL) Omissible<Optional<String>> imageUrl) {
        this.contentId = contentId;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    @JsonProperty(PROPERTY_CONTENT_ID)
    public Omissible<Optional<String>> getContentId() {
        return contentId;
    }

    @JsonProperty(PROPERTY_TITLE)
    public Omissible<Optional<String>> getTitle() {
        return title;
    }

    @JsonProperty(PROPERTY_DESCRIPTION)
    public Omissible<Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(PROPERTY_URL)
    public Omissible<Optional<String>> getUrl() {
        return url;
    }

    @JsonProperty(PROPERTY_IMAGE_URL)
    public Omissible<Optional<String>> getImageUrl() {
        return imageUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Optional<String>> contentId = Omissible.omitted();
        private Omissible<Optional<String>> title = Omissible.omitted();
        private Omissible<Optional<String>> description = Omissible.omitted();
        private Omissible<Optional<String>> url = Omissible.omitted();
        private Omissible<Optional<String>> imageUrl = Omissible.omitted();

        private Builder() {
        }

        public Builder withContentId(String contentId) {
            this.contentId = Omissible.of(Optional.ofNullable(contentId));
            return this;
        }

        public Builder withTitle(String title) {
            this.title = Omissible.of(Optional.ofNullable(title));
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

        public Builder withImageUrl(String imageUrl) {
            this.imageUrl = Omissible.of(Optional.ofNullable(imageUrl));
            return this;
        }

        public PromotionLinkContentRequest build() {
            return new PromotionLinkContentRequest(contentId, title, description, url, imageUrl);
        }

    }
}
