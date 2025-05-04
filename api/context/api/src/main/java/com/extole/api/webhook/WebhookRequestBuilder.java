package com.extole.api.webhook;

public interface WebhookRequestBuilder {

    WebhookRequestBuilder withUrl(String url);

    WebhookRequestBuilder withMethod(String method);

    WebhookRequestBuilder addHeader(String name, String value);

    WebhookRequestBuilder addHeader(String name, String[] values);

    WebhookRequestBuilder withBody(String body);

    WebhookRequestBuilder addUrlTemplateParameter(String name, String value);

    WebhookRequest build();

}
