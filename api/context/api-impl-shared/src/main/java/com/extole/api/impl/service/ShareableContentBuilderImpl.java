package com.extole.api.impl.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

import com.extole.api.service.PersonBuilderException;
import com.extole.api.service.ShareableContentBuilder;
import com.extole.person.service.shareable.ShareableBlockedUrlException;
import com.extole.person.service.shareable.ShareableContentDescriptionTooLongException;

public class ShareableContentBuilderImpl implements ShareableContentBuilder {

    private Optional<String> partnerContentId = Optional.empty();
    private Optional<String> title = Optional.empty();
    private Optional<URI> imageUrl = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<URI> url = Optional.empty();

    @Override
    public ShareableContentBuilder withPartnerContentId(String partnerContentId) {
        this.partnerContentId = Optional.ofNullable(partnerContentId);
        return this;
    }

    @Override
    public ShareableContentBuilder withTitle(String title) {
        this.title = Optional.ofNullable(title);
        return this;
    }

    @SuppressWarnings("unused")
    @Override
    public ShareableContentBuilder withImageUrl(String imageUrl) throws PersonBuilderException {
        try {
            new URL(imageUrl);
            this.imageUrl = Optional.of(new URI(imageUrl));
        } catch (MalformedURLException | URISyntaxException e) {
            throw new PersonBuilderException("Shareable imageUrl is invalid=" + imageUrl, e);
        }

        return this;
    }

    @Override
    public ShareableContentBuilder withDescription(String description) {
        this.description = Optional.ofNullable(description);
        return this;
    }

    @SuppressWarnings("unused")
    @Override
    public ShareableContentBuilder withUrl(String url) throws PersonBuilderException {
        try {
            new URL(url);
            this.url = Optional.of(new URI(url));
        } catch (MalformedURLException | URISyntaxException e) {
            throw new PersonBuilderException("Shareable url is invalid=" + url, e);
        }

        return this;
    }

    public void build(com.extole.person.service.shareable.ShareableContentBuilder contentBuilder)
        throws ShareableContentDescriptionTooLongException, ShareableBlockedUrlException {
        if (partnerContentId.isPresent()) {
            contentBuilder.withPartnerContentId(partnerContentId.get());
        }

        if (title.isPresent()) {
            contentBuilder.withTitle(title.get());
        }

        if (imageUrl.isPresent()) {
            contentBuilder.withImageUrl(imageUrl.get());
        }

        if (description.isPresent()) {
            contentBuilder.withDescription(description.get());
        }

        if (url.isPresent()) {
            contentBuilder.withUrl(url.get());
        }
    }

}
