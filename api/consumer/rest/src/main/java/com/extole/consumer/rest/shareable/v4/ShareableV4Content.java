package com.extole.consumer.rest.shareable.v4;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

@Deprecated // TODO remove ENG-10127
public class ShareableV4Content {

    private final String partnerContentId;
    private final String title;
    private final String imageUrl;
    private final String description;
    private final String url;

    @JsonCreator
    public ShareableV4Content(@Nullable @JsonProperty("partner_content_id") String partnerContentId,
        @Nullable @JsonProperty("title") String title, @Nullable @JsonProperty("image_url") String imageUrl,
        @Nullable @JsonProperty("description") String description, @Nullable @JsonProperty("url") String url) {
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

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
