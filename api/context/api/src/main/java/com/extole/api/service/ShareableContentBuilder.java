package com.extole.api.service;

public interface ShareableContentBuilder {

    ShareableContentBuilder withPartnerContentId(String partnerContentId);

    ShareableContentBuilder withTitle(String title);

    ShareableContentBuilder withImageUrl(String imageUrl) throws PersonBuilderException;

    ShareableContentBuilder withDescription(String description);

    ShareableContentBuilder withUrl(String url) throws PersonBuilderException;

}
