package com.extole.client.rest.event.stream;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.event.stream.EventStreamBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class EventStreamCreateRequest extends ComponentElementRequest {
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String TAGS = "tags";
    private static final String STOP_AT = "stop_at";

    private final BuildtimeEvaluatable<EventStreamBuildtimeContext, String> name;
    private final Omissible<BuildtimeEvaluatable<EventStreamBuildtimeContext, Optional<String>>> description;
    private final Omissible<Set<String>> tags;
    private final Omissible<Instant> stopAt;

    public EventStreamCreateRequest(
        @JsonProperty(NAME) BuildtimeEvaluatable<EventStreamBuildtimeContext, String> name,
        @JsonProperty(DESCRIPTION) Omissible<
            BuildtimeEvaluatable<EventStreamBuildtimeContext, Optional<String>>> description,
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @JsonProperty(STOP_AT) Omissible<Instant> stopAt,
        @JsonProperty(ComponentElementRequest.JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(ComponentElementRequest.JSON_COMPONENT_REFERENCES) Omissible<
            List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.stopAt = stopAt;
    }

    @JsonProperty(NAME)
    public BuildtimeEvaluatable<EventStreamBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<EventStreamBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(STOP_AT)
    public Omissible<Instant> getStopAt() {
        return stopAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {
        private BuildtimeEvaluatable<EventStreamBuildtimeContext, String> name;
        private Omissible<BuildtimeEvaluatable<EventStreamBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();
        private Omissible<Instant> stopAt = Omissible.omitted();

        private Builder() {
        }

        public Builder withName(BuildtimeEvaluatable<EventStreamBuildtimeContext, String> name) {
            this.name = name;
            return this;
        }

        public Builder
            withDescription(BuildtimeEvaluatable<EventStreamBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        private Builder withStopAt(Instant stopAt) {
            this.stopAt = Omissible.of(stopAt);
            return this;
        }

        public EventStreamCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new EventStreamCreateRequest(name, description, tags, stopAt, componentIds,
                componentReferences);
        }
    }
}
