package com.extole.api.impl.person;

import javax.annotation.Nullable;

import com.extole.api.person.ShareableContent;

public class ShareableContentImpl implements ShareableContent {

    private final String partnerContentId;
    private final String title;
    private final String imageUrl;
    private final String description;
    private final String url;

    public ShareableContentImpl(@Nullable com.extole.person.service.shareable.ShareableContent shareableContent) {
        if (shareableContent != null) {
            this.partnerContentId = shareableContent.getPartnerContentId().orElse(null);
            this.title = shareableContent.getTitle().orElse(null);
            this.imageUrl = shareableContent.getImageUrl().map(url -> url.toString()).orElse(null);
            this.description = shareableContent.getDescription().orElse(null);
            this.url = shareableContent.getUrl().map(url -> url.toString()).orElse(null);
        } else {
            this.partnerContentId = null;
            this.title = null;
            this.imageUrl = null;
            this.description = null;
            this.url = null;
        }
    }

    @Override
    public String getPartnerContentId() {
        return partnerContentId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getUrl() {
        return url;
    }

}
