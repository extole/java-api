package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CampaignComponentReferenceConfiguration {

    private static final String JSON_COMPONENT_ABSOLUTE_NAME = "absolute_name";
    private static final String JSON_COMPONENT_TAGS = "tags";
    private static final String JSON_SOCKET_NAMES = "socket_names";

    private final String absoluteName;
    private final Set<String> tags;
    private final List<String> socketNames;

    @JsonCreator
    public CampaignComponentReferenceConfiguration(@JsonProperty(JSON_COMPONENT_ABSOLUTE_NAME) String absoluteName,
        @JsonProperty(JSON_COMPONENT_TAGS) Set<String> tags,
        @JsonProperty(JSON_SOCKET_NAMES) List<String> socketNames) {
        this.absoluteName = absoluteName;
        this.tags = tags;
        this.socketNames = socketNames;
    }

    @JsonProperty(JSON_COMPONENT_ABSOLUTE_NAME)
    public String getAbsoluteName() {
        return absoluteName;
    }

    @JsonProperty(JSON_COMPONENT_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(JSON_SOCKET_NAMES)
    public List<String> getSocketNames() {
        return socketNames;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
