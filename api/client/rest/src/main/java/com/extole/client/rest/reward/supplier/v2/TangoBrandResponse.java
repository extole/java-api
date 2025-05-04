package com.extole.client.rest.reward.supplier.v2;

import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TangoBrandResponse {

    private static final String JSON_PROPERTY_BRAND_NAME = "brand_name";
    private static final String JSON_PROPERTY_DISCLAIMER = "disclaimer";
    private static final String JSON_PROPERTY_DESCRIPTION = "description";
    private static final String JSON_PROPERTY_IMAGE_URL = "image_url";
    private static final String JSON_PROPERTY_ITEMS = "items";

    private final String brandName;
    private final String disclaimer;
    private final String description;
    private final String imageUrl;
    private final List<TangoBrandItemResponse> items;

    @JsonCreator
    public TangoBrandResponse(@JsonProperty(JSON_PROPERTY_BRAND_NAME) String brandName,
        @JsonProperty(JSON_PROPERTY_DISCLAIMER) String disclaimer,
        @JsonProperty(JSON_PROPERTY_DESCRIPTION) String description,
        @Nullable @JsonProperty(JSON_PROPERTY_IMAGE_URL) String imageUrl,
        @JsonProperty(JSON_PROPERTY_ITEMS) List<TangoBrandItemResponse> items) {
        this.brandName = brandName;
        this.disclaimer = disclaimer;
        this.description = description;
        this.imageUrl = imageUrl;
        this.items = items;
    }

    @JsonProperty(JSON_PROPERTY_BRAND_NAME)
    public String getBrandName() {
        return brandName;
    }

    @JsonProperty(JSON_PROPERTY_DISCLAIMER)
    public String getDisclaimer() {
        return disclaimer;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_IMAGE_URL)
    public String getImageUrl() {
        return imageUrl;
    }

    @JsonProperty(JSON_PROPERTY_ITEMS)
    public List<TangoBrandItemResponse> getItems() {
        return items;
    }

}
