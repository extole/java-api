package com.extole.client.rest.shareable.v2;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO remove client-shareable-v2 ENG-10128
public final class UpdateShareableV2Request {

    private static final String JSON_PROPERTY_KEY = "key";
    private static final String JSON_PROPERTY_TARGET_URL = "target_url";
    private static final String JSON_PROPERTY_CONTENT = "content";
    private static final String JSON_PROPERTY_CONSUMER_EVENT = "consumer_event";
    private static final String JSON_PROPERTY_DATA = "data";
    private static final String JSON_PROPERTY_LABEL = "label";

    private final String key;
    private final String targetUrl;
    private final ShareableV2Content content;
    private final ConsumerEventV2Request consumerEvent;
    private final Map<String, String> data;
    private final String label;

    @JsonCreator
    public UpdateShareableV2Request(
        @Nullable @JsonProperty(JSON_PROPERTY_KEY) String key,
        @Nullable @JsonProperty(JSON_PROPERTY_TARGET_URL) String targetUrl,
        @Nullable @JsonProperty(JSON_PROPERTY_CONTENT) ShareableV2Content content,
        @Nullable @JsonProperty(JSON_PROPERTY_CONSUMER_EVENT) ConsumerEventV2Request consumerEvent,
        @Nullable @JsonProperty(JSON_PROPERTY_DATA) Map<String, String> data,
        @Nullable @JsonProperty(JSON_PROPERTY_LABEL) String label) {
        this.key = key;
        this.targetUrl = targetUrl;
        this.content = content;
        this.consumerEvent = consumerEvent;
        this.data = data == null ? Collections.emptyMap() : Collections.unmodifiableMap(data);
        this.label = label;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_CONSUMER_EVENT)
    public ConsumerEventV2Request getConsumerEvent() {
        return consumerEvent;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_CONTENT)
    public ShareableV2Content getContent() {
        return content;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_TARGET_URL)
    public String getTargetUrl() {
        return targetUrl;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_KEY)
    public String getKey() {
        return key;
    }

    @JsonProperty(JSON_PROPERTY_DATA)
    public Map<String, String> getData() {
        return data;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String key;
        private String targetUrl;
        private ShareableV2Content content;
        private ConsumerEventV2Request consumerEvent;
        private Map<String, String> data;
        private String label;

        private Builder() {
        }

        public Builder withKey(String key) {
            this.key = key;
            return this;
        }

        public Builder withTargetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
            return this;
        }

        public Builder withContent(ShareableV2Content content) {
            this.content = content;
            return this;
        }

        public Builder withConsumerEvent(ConsumerEventV2Request consumerEvent) {
            this.consumerEvent = consumerEvent;
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public Builder withLabel(String label) {
            this.label = label;
            return this;
        }

        public UpdateShareableV2Request build() {
            return new UpdateShareableV2Request(key, targetUrl, content, consumerEvent, data, label);
        }
    }
}
