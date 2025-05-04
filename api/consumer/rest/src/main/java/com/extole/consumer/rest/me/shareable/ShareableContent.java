package com.extole.consumer.rest.me.shareable;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ShareableContent {
    private final String partnerContentId;
    private final String title;
    private final String imageUrl;
    private final String description;
    private final String url;

    @JsonCreator
    public ShareableContent(
        @Nullable @JsonProperty("partner_content_id") String partnerContentId,
        @Nullable @JsonProperty("title") String title,
        @Nullable @JsonProperty("image_url") String imageUrl,
        @Nullable @JsonProperty("description") String description,
        @Nullable @JsonProperty("url") String url) {
        this.partnerContentId = partnerContentId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.url = url;
    }

    @Nullable
    @JsonProperty("partner_content_id")
    public String getPartnerContentId() {
        return partnerContentId;
    }

    @Nullable
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @Nullable
    @JsonProperty("image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    @Nullable
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @Nullable
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String partnerContentId;
        private String title;
        private String imageUrl;
        private String description;
        private String url;

        private Builder() {
        }

        public Builder withPartnerContentId(String val) {
            partnerContentId = val;
            return this;
        }

        public Builder withTitle(String val) {
            title = val;
            return this;
        }

        public Builder withImageUrl(String val) {
            imageUrl = val;
            return this;
        }

        public Builder withDescription(String val) {
            description = val;
            return this;
        }

        public Builder withUrl(String val) {
            url = val;
            return this;
        }

        public ShareableContent build() {
            return new ShareableContent(partnerContentId, title, imageUrl, description, url);
        }
    }
}
