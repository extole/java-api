package com.extole.client.rest.promotion;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PromotionLinkContentResponse {
    private static final String PROPERTY_CONTENT_ID = "content_id";
    private static final String PROPERTY_TITLE = "title";
    private static final String PROPERTY_DESCRIPTION = "description";
    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_IMAGE_URL = "image_url";

    private final Optional<String> contentId;
    private final Optional<String> title;
    private final Optional<String> description;
    private final Optional<String> url;
    private final Optional<String> imageUrl;

    @JsonCreator
    public PromotionLinkContentResponse(
        @JsonProperty(PROPERTY_CONTENT_ID) Optional<String> contentId,
        @JsonProperty(PROPERTY_TITLE) Optional<String> title,
        @JsonProperty(PROPERTY_DESCRIPTION) Optional<String> description,
        @JsonProperty(PROPERTY_URL) Optional<String> url,
        @JsonProperty(PROPERTY_IMAGE_URL) Optional<String> imageUrl) {
        this.contentId = contentId;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    @JsonProperty(PROPERTY_CONTENT_ID)
    public Optional<String> getContentId() {
        return contentId;
    }

    @JsonProperty(PROPERTY_TITLE)
    public Optional<String> getTitle() {
        return title;
    }

    @JsonProperty(PROPERTY_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(PROPERTY_URL)
    public Optional<String> getUrl() {
        return url;
    }

    @JsonProperty(PROPERTY_IMAGE_URL)
    public Optional<String> getImageUrl() {
        return imageUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Optional<String> contentId = Optional.empty();
        private Optional<String> title = Optional.empty();
        private Optional<String> description = Optional.empty();
        private Optional<String> url = Optional.empty();
        private Optional<String> imageUrl = Optional.empty();

        private Builder() {
        }

        public Builder withContentId(String contentId) {
            this.contentId = Optional.ofNullable(contentId);
            return this;
        }

        public Builder withTitle(String title) {
            this.title = Optional.ofNullable(title);
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

        public Builder withImageUrl(String imageUrl) {
            this.imageUrl = Optional.ofNullable(imageUrl);
            return this;
        }

        public PromotionLinkContentResponse build() {
            return new PromotionLinkContentResponse(contentId, title, description, url, imageUrl);
        }

    }
}
