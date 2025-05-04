package com.extole.client.rest.audience;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.audience.AudienceBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@Schema
public class AudienceCreateRequest extends ComponentElementRequest {

    private static final String NAME = "name";
    private static final String ENABLED = "enabled";
    private static final String TAGS = "tags";

    private final BuildtimeEvaluatable<AudienceBuildtimeContext, String> name;
    private final Omissible<BuildtimeEvaluatable<AudienceBuildtimeContext, Boolean>> enabled;
    private final Omissible<Set<String>> tags;

    public AudienceCreateRequest(@JsonProperty(NAME) BuildtimeEvaluatable<AudienceBuildtimeContext, String> name,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<AudienceBuildtimeContext, Boolean>> enabled,
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.name = name;
        this.enabled = enabled;
        this.tags = tags;
    }

    @JsonProperty(NAME)
    public BuildtimeEvaluatable<AudienceBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(ENABLED)
    public Omissible<BuildtimeEvaluatable<AudienceBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static AudienceCreateRequest.Builder builder() {
        return new AudienceCreateRequest.Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private BuildtimeEvaluatable<AudienceBuildtimeContext, String> name;
        private Omissible<BuildtimeEvaluatable<AudienceBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();

        private Builder() {
            super();
        }

        public Builder withName(BuildtimeEvaluatable<AudienceBuildtimeContext, String> name) {
            this.name = name;
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<AudienceBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public AudienceCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new AudienceCreateRequest(name, enabled, tags, componentIds, componentReferences);
        }

    }

}
