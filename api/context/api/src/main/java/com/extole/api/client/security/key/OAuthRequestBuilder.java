package com.extole.api.client.security.key;

public interface OAuthRequestBuilder {

    OAuthRequestBuilder withUrl(String url);

    OAuthRequestBuilder addHeader(String name, String value);

    OAuthRequestBuilder addHeader(String name, String[] values);

    OAuthRequestBuilder withBody(String body);

    OAuthRequest build();

}
