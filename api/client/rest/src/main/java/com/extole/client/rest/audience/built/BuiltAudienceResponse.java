package com.extole.client.rest.audience.built;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.audience.Audience;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class BuiltAudienceResponse extends ComponentElementResponse {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String ENABLED = "enabled";
    private static final String TAGS = "tags";

    private final Id<Audience> id;
    private final String name;
    private final boolean enabled;
    private final Set<String> tags;

    @JsonCreator
    public BuiltAudienceResponse(@JsonProperty(ID) Id<Audience> id,
        @JsonProperty(NAME) String name,
        @JsonProperty(ENABLED) boolean enabled,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.tags = ImmutableSet.copyOf(tags);
    }

    @JsonProperty(ID)
    public Id<Audience> getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(ENABLED)
    public boolean isEnabled() {
        return enabled;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
