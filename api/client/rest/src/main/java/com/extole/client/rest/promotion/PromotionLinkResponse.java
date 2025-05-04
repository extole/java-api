package com.extole.client.rest.promotion;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PromotionLinkResponse {
    private static final String PROPERTY_CODE = "code";
    private static final String PROPERTY_KEY = "key";
    private static final String PROPERTY_PROGRAM_URL = "program_url";
    private static final String PROPERTY_PROMOTION_URL = "promotion_url";
    private static final String PROPERTY_CONTENT = "content";
    private static final String PROPERTY_DATA = "data";
    private static final String PROPERTY_LABEL = "label";
    private static final String PROPERTY_DESCRIPTION = "description";

    private final String code;
    private final String key;
    private final String programUrl;
    private final String promotionUrl;
    private final PromotionLinkContentResponse content;
    private final Map<String, String> data;
    private final Optional<String> label;
    private final Optional<String> description;

    @JsonCreator
    public PromotionLinkResponse(
        @JsonProperty(PROPERTY_CODE) String code,
        @JsonProperty(PROPERTY_KEY) String key,
        @JsonProperty(PROPERTY_PROGRAM_URL) String programUrl,
        @JsonProperty(PROPERTY_PROMOTION_URL) String promotionUrl,
        @JsonProperty(PROPERTY_CONTENT) PromotionLinkContentResponse content,
        @JsonProperty(PROPERTY_DATA) Map<String, String> data,
        @JsonProperty(PROPERTY_LABEL) Optional<String> label,
        @JsonProperty(PROPERTY_DESCRIPTION) Optional<String> description) {
        this.code = code;
        this.key = key;
        this.programUrl = programUrl;
        this.promotionUrl = promotionUrl;
        this.content = content;
        this.data = data;
        this.label = label;
        this.description = description;
    }

    @JsonProperty(PROPERTY_CODE)
    public String getCode() {
        return code;
    }

    @JsonProperty(PROPERTY_KEY)
    public String getKey() {
        return key;
    }

    @JsonProperty(PROPERTY_PROGRAM_URL)
    public String getProgramUrl() {
        return programUrl;
    }

    @JsonProperty(PROPERTY_PROMOTION_URL)
    public String getPromotionUrl() {
        return promotionUrl;
    }

    @JsonProperty(PROPERTY_CONTENT)
    public PromotionLinkContentResponse getContent() {
        return content;
    }

    @JsonProperty(PROPERTY_DATA)
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(PROPERTY_LABEL)
    public Optional<String> getLabel() {
        return label;
    }

    @JsonProperty(PROPERTY_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
