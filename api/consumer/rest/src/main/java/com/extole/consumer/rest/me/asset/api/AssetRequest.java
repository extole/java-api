package com.extole.consumer.rest.me.asset.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetRequest {
    private static final String NAME = "name";
    private static final String TAGS = "tags";
    private static final String DATA_TYPE = "data_type";

    private final String name;
    private final List<String> tags;
    private final Optional<MeAssetType> dataType;

    public AssetRequest(
        @Nullable @JsonProperty(NAME) String name,
        @Nullable @JsonProperty(TAGS) List<String> tags,
        @Nullable @JsonProperty(DATA_TYPE) MeAssetType dataType) {
        this.name = name;
        this.tags = tags != null ? tags : Collections.emptyList();
        this.dataType = Optional.ofNullable(dataType);
    }

    @Nullable
    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(TAGS)
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty(DATA_TYPE)
    public Optional<MeAssetType> getDataType() {
        return dataType;
    }
}
