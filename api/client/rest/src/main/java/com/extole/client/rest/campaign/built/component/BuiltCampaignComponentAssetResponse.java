package com.extole.client.rest.campaign.built.component;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class BuiltCampaignComponentAssetResponse {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_FILENAME = "filename";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_DESCRIPTION = "description";

    private final String id;
    private final String name;
    private final String filename;
    private final Set<String> tags;
    private final Optional<String> description;

    @JsonCreator
    public BuiltCampaignComponentAssetResponse(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_FILENAME) String filename,
        @JsonProperty(JSON_TAGS) Set<String> tags,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description) {
        this.id = id;
        this.name = name;
        this.filename = filename;
        this.tags = Collections.unmodifiableSet(tags);
        this.description = description;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_FILENAME)
    public String getFilename() {
        return filename;
    }

    @JsonProperty(JSON_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
